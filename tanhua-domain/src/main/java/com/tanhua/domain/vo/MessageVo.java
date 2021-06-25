package com.tanhua.domain.vo;

import lombok.Data;
    /**
     * 点赞喜欢评论列表返回
     */
@Data
public class MessageVo {
    private String id;
    private String avatar;
    private String nickname;
    private String createDate;
}