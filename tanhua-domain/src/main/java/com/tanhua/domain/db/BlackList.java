package com.tanhua.domain.db;
import lombok.Data;
    /**
     * 黑名单实体
     */
@Data
public class BlackList extends BasePojo {
    private Long id;
    private Long userId;
    private Long blackUserId;
}