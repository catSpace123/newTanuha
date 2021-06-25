package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


/**
 * 语音表实体
 */
@Data
@Document(collection = "voice")
public class Voice implements Serializable {
   private ObjectId  id;         //主键
   @Indexed
   private long userId;		    //发布人id
   private String voiceUrl;	    //语音url
   private String gender ;       //性别
   private long createTime;
}
