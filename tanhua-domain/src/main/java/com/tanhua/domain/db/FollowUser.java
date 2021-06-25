package com.tanhua.domain.db;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 关注好友的实体类
 */
@Data
@Document(collection = "follow_user")
public class FollowUser implements Serializable {

    private ObjectId id; //主键id
    private Long userId; //用户id
    private Long followUserId; //关注的用户id
    private Long created; //关注时间
}