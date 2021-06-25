package com.tanhua.manage.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户返回的动态的 vo实体
 */
@Data
public class MomentManagerVo implements Serializable {
    private String id; //动态id

    private Integer userId; //用户id
    private String avatar; //头像
    private String nickname; //昵称

    private Long createDate; //发布时间
    private String textContent; //文字动态
    private String[] imageContent; //图片动态
    private Integer state;          // 审核状态，1为待审核，2为自动审核通过，3为待人工审核，4为人工审核拒绝，5为人工审核通过，6为自动审核拒绝
    private int likeCount; //点赞数
    private int commentCount; //评论数
    private int loveCount; //喜欢数

}