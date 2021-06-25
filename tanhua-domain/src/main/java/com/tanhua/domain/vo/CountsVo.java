package com.tanhua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回数量
 */
@Data
public class CountsVo implements Serializable {
    private Long eachLoveCount; //互相喜欢
    private Long loveCount; //喜欢
    private Long fanCount; //粉丝
}