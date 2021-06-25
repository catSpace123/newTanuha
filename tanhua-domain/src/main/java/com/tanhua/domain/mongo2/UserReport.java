package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 个人报告表
 */
@Data
@Document(collection = "user_report")
public class UserReport implements Serializable {
    private ObjectId id;
    private long userID;  //用户id
    private String reportId;//详细报告id
    private long dateTime;
}
