package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;


/**
 * 测灵魂返回问题选项试题
 */
@Data
public class Options implements Serializable {
    private String id;  //选项id
    private String option;//选项
}
