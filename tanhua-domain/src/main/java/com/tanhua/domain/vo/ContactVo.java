package com.tanhua.domain.vo;
import lombok.Data;

/**
 * 返回联系人列表
 */
@Data
public class ContactVo {
    private Long id;
    private String userId;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;
}