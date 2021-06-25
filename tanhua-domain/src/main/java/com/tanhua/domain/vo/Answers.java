package com.tanhua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**\
 * 测灵魂答案接收实体
 */
@Data
public class Answers implements Serializable {
    private String  questionId; //试题编号
    private String  optionId;   //选项编号
}
