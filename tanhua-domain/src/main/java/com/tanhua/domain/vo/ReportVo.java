package com.tanhua.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 测灵魂查看结果返回实体
 */
@Data
public class ReportVo {
    private String conclusion; //鉴定结果
    private String cover;       //鉴定图片
    private List<DimensionsVo> dimensions; //维度
    private List<SimilarYou> similarYou;//与你相似
}
