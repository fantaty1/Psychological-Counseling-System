package com.cqust.psychologicalcounseling.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 心理测评量表实体类
 */
@Data
public class AssessmentScale {
    /**
     * 量表ID
     */
    private Long id;
    
    /**
     * 量表名称
     */
    private String name;
    
    /**
     * 量表描述
     */
    private String description;
    
    /**
     * 分类（如：情绪、人际、压力）
     */
    private String category;
    
    /**
     * 题目数量
     */
    private Integer questionCount;
    
    /**
     * 预计完成时间（分钟）
     */
    private Integer estimatedTime;
    
    /**
     * 答题说明
     */
    private String instructions;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 排序权重
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
