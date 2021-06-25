package com.tanhua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表 返回实体
 */
@Data
public class FriendVo implements Serializable {

    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;
    private String education;
    private Integer marriage; //婚姻状态（0未婚，1已婚）
    private Integer matchRate; //匹配度
}