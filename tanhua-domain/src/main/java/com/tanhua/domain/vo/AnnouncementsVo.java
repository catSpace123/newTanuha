package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告管理实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementsVo {
    private String id;
    private String title;
    private String description;
    private String createDate;
}
