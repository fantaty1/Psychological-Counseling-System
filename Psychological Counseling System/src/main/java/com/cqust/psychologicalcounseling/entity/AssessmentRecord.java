package com.cqust.psychologicalcounseling.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户测评记录实体类
 */
@Data
public class AssessmentRecord {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 量表ID
     */
    private Long scaleId;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 结果等级（如：正常、轻度、中度、重度）
     */
    private String resultLevel;
    
    /**
     * 结果摘要
     */
    private String resultSummary;
    
    /**
     * 答题用时（秒）
     */
    private Integer duration;
    
    /**
     * 状态：in_progress-进行中 completed-已完成
     */
    private String status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
