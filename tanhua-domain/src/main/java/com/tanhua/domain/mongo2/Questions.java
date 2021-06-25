package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 试题表
 */
@Data
@Document(collection ="test_Questions" )
public class Questions implements Serializable {
   private ObjectId  id;           // 主键  试题编号
   private String question	;   // 		试题名称
   private Options [] options;	 //		试题选项
   private  String level;       //		string 	等级(初级,中级,高级)
}
