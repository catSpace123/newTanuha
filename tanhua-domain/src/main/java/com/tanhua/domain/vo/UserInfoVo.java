package com.tanhua.domain.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 用来接收用户传过来的数据 跟存入数据库的不是一个实体，是为了解耦。
 */
@Data
public class UserInfoVo implements Serializable {
    private Long id; //用户id
    private String nickname; //昵称
    private String avatar; //用户头像
    private String birthday; //生日
    private String gender; //性别
    private String age; //年龄
    private String city; //城市
    private String income; //收入
    private String education; //学历
    private String profession; //行业
    private String tags;        //用户标签
    private Integer marriage; //婚姻状态
    private String state ; //状态
    private long created;
}