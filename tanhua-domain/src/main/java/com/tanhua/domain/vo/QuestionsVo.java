package com.tanhua.domain.vo;

import com.tanhua.domain.mongo2.Options;
import lombok.Data;


/**
 * 测灵魂返回问题实体
 */
@Data
public class QuestionsVo {

    private String id ;//试题编号
    private String question;//题目
    private Options[] options;  //题目选项
}
