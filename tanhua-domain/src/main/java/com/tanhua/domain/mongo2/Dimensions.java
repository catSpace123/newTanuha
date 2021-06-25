package com.tanhua.domain.mongo2;

import lombok.Data;

import java.io.Serializable;

/**
 * 维度
 */
@Data
public class Dimensions implements Serializable {
    private String key;  //维度项（外向，判断，抽象，理性）
    private String value;//维度值
}
