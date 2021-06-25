package com.tanhua.domain.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "recommend_user")  //将这个实体对象跟mongodb的集合建立映射关系
public class RecommendUser implements Serializable {

    @Id   //标注为id主键
    private ObjectId id; //主键id
    @Indexed  //添加索引
    private Long userId; //推荐的用户id
    private Long toUserId; //登录用户id
    @Indexed
    private Double score =0d; //推荐得分
    private String date; //日期
}