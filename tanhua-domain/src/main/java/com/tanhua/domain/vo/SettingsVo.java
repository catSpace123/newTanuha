package com.tanhua.domain.vo;

import lombok.Data;

/**
 * 前端返回通知设置实体
 */
@Data
public class SettingsVo {
    private Long id;
    private String strangerQuestion;
    private String phone;
    private Boolean likeNotification;
    private Boolean pinglunNotification;
    private Boolean gonggaoNotification;  //公告
}