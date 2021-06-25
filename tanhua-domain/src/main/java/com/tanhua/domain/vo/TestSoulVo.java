package com.tanhua.domain.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 测灵魂返回实体vo
 */
@Data
@Document(collection = "test_sou")
public class TestSoulVo implements Serializable {
    private String id ;//问卷编号
    private String name;// 初级灵魂题,中级灵魂题,高级灵魂题
    private String cover;//封面
    private String level;//级别  初级,中级,高级
    private Integer star;//星别（例如：2颗星，3颗星，5颗星）
    private List<QuestionsVo> questions;//试题
    private Integer isLock;//是否锁住（0解锁，1锁住）
    private String reportId;//最新报告id
}
