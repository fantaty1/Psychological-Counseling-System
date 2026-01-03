package com.cqust.psychologicalcounseling.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 测评题目实体类
 */
@Data
public class AssessmentQuestion {
    /**
     * 题目ID
     */
    private Long id;
    
    /**
     * 所属量表ID
     */
    private Long scaleId;
    
    /**
     * 题目序号
     */
    private Integer questionNo;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 题目类型：single-单选 multiple-多选
     */
    private String questionType;
    
    /**
     * 选项（JSON数组）
     */
    private String options;
    
    /**
     * 是否反向计分：0-否 1-是
     */
    private Integer reverseScore;
    
    /**
     * 维度/因子
     */
    private String dimension;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
