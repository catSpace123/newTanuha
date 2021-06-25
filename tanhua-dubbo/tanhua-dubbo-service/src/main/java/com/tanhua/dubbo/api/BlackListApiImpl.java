package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.BlackListMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 黑名单列表实现类
 */
@Service
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private BlackListMapper blackListMapper;
    /**
     * 黑名单分页查询
     * @param page  //当前页码
     * @param pagesize //每页显示条数
     * @param userId  当前用户id
     * @return
     */
    @Override
    public PageResult<UserInfo> findBlackPage(Integer page, Integer pagesize, Long userId) {
        //先用mybatisplus 的page对象封装
        Page page1 = new Page<>(page,pagesize);

        //调dao接口
        IPage<UserInfo> iPage = blackListMapper.findPage(page1,userId);
        //返回查询到的记录数，每页有几条，多少页，当前页，当前结果
        System.out.println(iPage.getRecords()+"========");
        return new PageResult(iPage.getTotal(),iPage.getSize(),iPage.getPages(),iPage.getCurrent(),iPage.getRecords());
    }



    /**
     *
     * @param uid  要删除的黑名单用户id
     * @param userId 根据用户id删除对应的黑名单用户
     */
    @Override
    public void deleteByUserId(Long uid, Long userId) {
        LambdaQueryWrapper<BlackList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlackList::getBlackUserId,uid)
                .eq(BlackList::getUserId,userId);
        blackListMapper.delete(queryWrapper);
    }
}
