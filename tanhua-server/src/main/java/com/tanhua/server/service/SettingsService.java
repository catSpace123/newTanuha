package com.tanhua.server.service;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.api.UserQuestionApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 通用设置业务层
 */
@Service
public class SettingsService {

    @Reference
    private UserQuestionApi userQuestionApi;

    @Reference
    private SettingsApi settingsApi;

    @Reference
    private BlackListApi blackListApi;
    /**
     * 通用设置，查询陌生人问题，通知设置，电话号码
     */
    public SettingsVo findSettings() {
        //1根据用户id查询问题
        Long userId = UserHolder.getUserId();

        //用来返回数据的封装
        SettingsVo settingsVo = new SettingsVo();

        Question  question = userQuestionApi.findQuestion(userId);
        //2判断如果为空就设置默认值

        if(StringUtils.isEmpty(question) || StringUtils.isEmpty(question.getTxt())){
            //设置默认值
            settingsVo.setStrangerQuestion("喝酒吗？");
        }else{
            //3如果不为空就用查出来的的数据
            settingsVo.setStrangerQuestion(question.getTxt());
        }
        //.4根据用户id查询通用设置 得到3个通知信息（调用服务提供者）
        Settings settings = settingsApi.findSettings(userId);

        //如果为空设置默认值
        if(StringUtils.isEmpty(settings)){
            settingsVo.setGonggaoNotification(false);
            settingsVo.setLikeNotification(false);
            settingsVo.setPinglunNotification(false);
        }else{
            //不为空就用数据库查出来的
            BeanUtils.copyProperties(settings,settingsVo);
        }

        //5封装电话号码  在thrandlocal中获取电话号码给返回对象赋值
        settingsVo.setPhone(UserHolder.getUser().getMobile());
        //6当前用户id也封装返回
        settingsVo.setId(UserHolder.getUserId());

        return settingsVo;
    }


    /**
     * 更新或添加陌生人问题
     */
    public void updateOrSaveQuestions(String questions) {
        Long userId = UserHolder.getUserId();
        //1 更据userid查询当前用户是否有陌生人问题的记录信息
        Question question = userQuestionApi.findQuestion(userId);
        //2判断当前对象是否为空
        if(!StringUtils.isEmpty(question)){
            //如果不为空表示当前用户有记录，则现在更新记录
            question.setTxt(questions);
            userQuestionApi.updateQuestion(question);
        }else{
            //3为空就添加陌生人问题记录
            question = new Question();  //防止空指针异常

            question.setUserId(userId);
            question.setTxt(questions);

            userQuestionApi.saveQuestions(question);
        }



    }


    /**
     * 更新或添加通知设置记录
     */
    public void updateOrSaveSettings(Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification) {
        //1 先根据用户id查询是否有这条设置记录重在
        Long userId = UserHolder.getUserId();

        Settings settings = settingsApi.findSettings(userId);
        //2判断是否为空
        if(!StringUtils.isEmpty(settings)){
            //如果不为空则修改
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            //调用业务修改
            settingsApi.updateSettings(settings);
        }else{
            //如果为空就根据用户id添加记录
            settings = new Settings();
            settings.setUserId(userId);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.saveSettings(settings);
        }
    }


    /**
     * 黑名单分页查询
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findBlackListPage(Integer page, Integer pagesize) {

        Long userId = UserHolder.getUserId();
        //调用服务提供者
        PageResult pageResult = blackListApi.findBlackPage(page, pagesize, userId);
        return pageResult;
    }

    /**
     * 根据用户id删除对应的黑名单用户
     * @param uid  要删除的用户id
     */
    public void deleteBlackListByUserId(Long uid) {
        //UserHolder.getUserId()   当前用户id
        blackListApi.deleteByUserId(uid,UserHolder.getUserId());
    }
}
