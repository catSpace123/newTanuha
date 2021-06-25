package com.tanhua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回访客的实体
 */
@Data
public class VisitorVo implements Serializable {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private Integer fateValue;
}