package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.FollowUser;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.SmallVideosApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 小视频业务层
 */
@Service
public class SmallVideosService {

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private SmallVideosApi smallVideosApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 小视频上传
     * @param videoThumbnail  封面图片信息
     * @param videoFile        小视频
     * @return
     */
    @CacheEvict(value =  "VideosList",allEntries = true)   //添加的时候缓冲  等查询的时候在缓冲
    public void upload(MultipartFile videoThumbnail, MultipartFile videoFile) {
        try {


            //a 调用阿里云oss把视频封面上传上去
            String originalFilename = videoThumbnail.getOriginalFilename();
            //获取到上传成功后的图片地址
            String picUrl = ossTemplate.upload(originalFilename, videoThumbnail.getInputStream());
            //b 上传视频到fastDfs
            //截取文件的后缀
            int lastIndexOf = videoFile.getOriginalFilename().lastIndexOf(".");
            String suff = videoFile.getOriginalFilename().substring(lastIndexOf + 1);
            //获取到文件中在服务器中存储的位置
            StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suff, null);
            //在获取ip地址更文件位置拼接，就得到能够访问视频的地址
            String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
            System.out.println(videoUrl);
            //创建保存的对象
            Video video = new Video();
            video.setUserId(UserHolder.getUserId());  //当前用户id
            video.setVid(123L);                         //处境id  没用到
            video.setText("上传视频啦");                 //前端没有处理上传，给默认值
            video.setPicUrl(picUrl);                   //图片地址
            video.setVideoUrl(videoUrl);                //视频地址
            smallVideosApi.upload(video);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    /**
     * 小视频列表分页查询
     * @param page
     * @param pagesize
     * @return
     */
    @Cacheable(value = "VideosList",key = "#page+#pagesize") //缓冲返回结果
    public PageResult<VideoVo> queryVideosList(Integer page, Integer pagesize) {
        Long currentUserId = UserHolder.getUserId();
        //创建返回的对象实体
        PageResult<VideoVo> pageResult = new PageResult<>();
        List<VideoVo> list = new ArrayList<>();

        //a 根据分页条件调用服务提供者查询视频信息
     PageResult<Video> VideoPageResult = smallVideosApi.queryVideosList(page,pagesize);

     //判断是否非空
        if(VideoPageResult == null || CollectionUtils.isEmpty(VideoPageResult.getItems())){
            return pageResult;
        }

        for (Video video : VideoPageResult.getItems()) {
            VideoVo videoVo = new VideoVo();
            //b 根据查询到的userid查询userinfo表，查询用户的个人信息
            UserInfo userInfo = userInfoApi.findByUserId(video.getUserId());


            BeanUtils.copyProperties(video,videoVo);
            videoVo.setAvatar(userInfo.getAvatar());  //头像
            videoVo.setNickname(userInfo.getNickname());  //昵称
            videoVo.setId(video.getId().toHexString());  //视频id
            videoVo.setCover(video.getPicUrl());        //封面
            if(!StringUtils.isEmpty(video.getText())){  //签名
                videoVo.setSignature(video.getText());
            }else{
                videoVo.setSignature("哇哈哈");  //如果为空给他个默认值
            }
            videoVo.setHasLiked(0); //是否已赞（0）没攒
            if(StringUtils.isEmpty(redisTemplate.opsForValue().get("userFocus_"+currentUserId+video.getUserId()))){
                videoVo.setHasFocus(0); // 否关注
            }else{
                videoVo.setHasFocus(1); // 关注
            }
            list.add(videoVo);
        }

        //封装返回
        BeanUtils.copyProperties(VideoPageResult,pageResult);
        pageResult.setItems(list);
        return pageResult;
    }

    /**
     * 视频用户关注
     * @return  friend 要关注的用户id
     */
    public void userFocus(Long friendID) {
        Long currentUserId = UserHolder.getUserId();  //当前用户id
        //先判断上传的用户id是否跟当前用户id相等 如果相等就不进行下面的代码操作，（意思就是用户不能自己关注自己）
        if(friendID == currentUserId){
            return;
        }

        //调用服务提供者保存记录
        smallVideosApi.saveUserFocus(friendID,currentUserId);

        //存入redis
        redisTemplate.opsForValue().set("userFocus_"+currentUserId+friendID,friendID.toString());
    }


    /**
     * 视频用户取消关注
     * @return  friendID 要关注的用户id
     */
    public void userUnFocus(Long friendID) {

        Long currentUserId = UserHolder.getUserId();  //当前用户id

        //调用服务提供者删除记录

        smallVideosApi.deleteUserFocus(friendID,currentUserId);

        //删除redis中的key
        redisTemplate.delete("userFocus_"+currentUserId+friendID);
    }
}
