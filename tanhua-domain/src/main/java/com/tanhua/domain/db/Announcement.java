package com.tanhua.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告管理实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BasePojo{
        private Integer id;
        private String title;
        private String description;
}
