package com.tanhua.domain.vo;

import lombok.Data;

/**
 * 维度
 */
@Data
public class DimensionsVo {
    private String key;  //维度项（外向，判断，抽象，理性）
    private String value;//维度值
}
