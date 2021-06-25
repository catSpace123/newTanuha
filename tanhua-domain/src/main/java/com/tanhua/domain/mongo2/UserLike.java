package com.tanhua.domain.mongo2;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 喜欢，粉丝的实体类
 */
@Data
@Document(collection = "user_like")
public class UserLike implements Serializable {

    private ObjectId id;
    @Indexed
    private Long userId; //用户id，自己
    @Indexed
    private Long likeUserId; //喜欢的用户id，对方

    private Long created; //创建时间
}