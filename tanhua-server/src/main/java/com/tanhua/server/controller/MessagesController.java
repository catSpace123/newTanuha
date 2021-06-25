package com.tanhua.server.controller;


import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.vo.AnnouncementsVo;
import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.MessageVo;
import com.tanhua.server.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 消息管理控制层
 */
@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    /**
     * 分页查询公告
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/announcements",method = RequestMethod.GET)
    public ResponseEntity findAnnouncements(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize){


      PageResult<AnnouncementsVo> pageResult = messagesService.findAnnouncements(page,pagesize);
        return ResponseEntity.ok(pageResult);

    }

    /**
     * 添加好友关系
     * @param param
     * @return
     */
    @RequestMapping(value = "/contacts",method = RequestMethod.POST)
    public ResponseEntity contacts(@RequestBody Map<String,Long> param){
        Long friendUserId = param.get("userId");
        messagesService.contacts(friendUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询联系人列表
     * @param page
     * @param pagesize
     * @param keyword  查询条件
     * @return
     */
    @RequestMapping(value = "/contacts",method = RequestMethod.GET)
    public ResponseEntity queryContacts(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize,
                                        String keyword){

        PageResult<ContactVo> pageResult  = messagesService.queryContacts(page,pagesize,keyword);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 分页查询点赞列表
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/likes",method = RequestMethod.GET)
    public ResponseEntity likes(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10")Integer pagesize){

        PageResult<MessageVo> pageResult = messagesService.querylikes(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 分页查询喜欢列表
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/loves",method = RequestMethod.GET)
    public ResponseEntity loves(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10")Integer pagesize){

        PageResult<MessageVo> pageResult = messagesService.queryloves(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 评论列表
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/comments",method = RequestMethod.GET)
    public ResponseEntity comments(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10")Integer pagesize){

        PageResult<MessageVo> pageResult = messagesService.querycomments(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }
}
