package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 评分表
 */
@Data
@Document(collection = "score")
public class Score implements Serializable {
   private ObjectId id;          //		主键
   private long userId;          //		long	用户id
   private double score;         //		integer	分数
   private String levelId;       //		string	等级id
   private long createTime;
}
