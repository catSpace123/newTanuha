package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 评分依据表
 */
@Data
@Document(collection = "scoring_basis")
public class ScoringBasis {
   private ObjectId id;	            //		主键
   private Integer questionId;	    //	integer	问题编号
   private Integer option;	        //	integer	选项编号
   private Integer optionScore;	    //	integer	选项得分
}
