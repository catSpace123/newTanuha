package com.tanhua.domain.mongo2;

import com.tanhua.domain.vo.DimensionsVo;
import com.tanhua.domain.vo.SimilarYou;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 测灵魂查看结果数据库实体
 */
@Data
@Document(collection = "report")
public class Report implements Serializable {
    private ObjectId id;
    private Double score;   //分数
    private String conclusion; //鉴定结果
    private String cover;       //鉴定图片
    private List<Dimensions> dimensions; //维度
}
