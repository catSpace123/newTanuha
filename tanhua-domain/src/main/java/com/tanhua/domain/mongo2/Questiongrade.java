package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.security.PrivateKey;

/**
 * 测灵魂:  试题等级表
 */
@Data
@Document(collection ="question_grade")
public class Questiongrade implements Serializable {
   private  ObjectId id;   //  			主键
   private  String name;        // 		string	问卷名称(初级灵魂题,中级灵魂题,高级灵魂题)
   private  String cover;       //		string	封面
   private  String level;       //		string 	等级(初级,中级,高级)
   private  Integer star;        //		integer 星级(最大值5,最小值2)
   private  Integer isLock;        //		integer	是否锁住(0解锁,1锁住)
}
