package com.tanhua.dubbo.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.db.PageResult;
import com.tanhua.dubbo.mapper.MessagesMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息服务提供者接口
 */
@Service
public class MessagesApiImpl implements MessagesApi{

    @Autowired
    private MessagesMapper messagesMapper;
    /**
     * 分页查询公告
     * @param page
     * @param pagesize
     * @return
     */

    @Override
    public PageResult<Announcement> findAnnouncements(Integer page, Integer pagesize) {

        //查询公告分页
        Page page1 = new Page<>(page,pagesize);
        QueryWrapper<Announcement> query = new QueryWrapper<>();
        IPage<Announcement> selectPage = messagesMapper.selectPage(page1,query);
        return  new PageResult(selectPage.getTotal(),selectPage.getSize(),selectPage.getPages(),selectPage.getCurrent(),
                selectPage.getRecords());
    }
}
