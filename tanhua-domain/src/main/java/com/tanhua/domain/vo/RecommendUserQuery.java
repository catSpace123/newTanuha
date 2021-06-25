package com.tanhua.domain.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 用来接收推荐用户的分页参数的实体对象
 */
@Data
public class RecommendUserQuery implements Serializable {


    private Integer page;  //当前页码
    private Integer pagesize;  //页面大小
    private String  gender;   //性别
    private String  lastLogin; //近期登录时间
    private Integer age;
    private String  city;
    private String  education;   //学历
}
