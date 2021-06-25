package com.tanhua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接收语音返回实体
 */
@Data
public class VoiceVo implements Serializable {

    private Integer id ;            //用户id
    private String avatar ;         //用户头像
    private String  nickname;       //昵称
    private String gender;          //性别 man woman
    private Integer age;            //年龄
    private String soundUrl;        //语音地址
    private Integer remainingTimes; //剩余次数
}
