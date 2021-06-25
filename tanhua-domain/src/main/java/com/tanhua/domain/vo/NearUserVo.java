package com.tanhua.domain.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来封装返回数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearUserVo {
    private Long userId;
    private String avatar;
    private String nickname;
}