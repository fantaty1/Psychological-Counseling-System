package com.cqust.psychologicalcounseling.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测评结果响应DTO
 */
@Data
public class AssessmentResultDTO {
    /**
     * 记录ID
     */
    private Long recordId;
    
    /**
     * 量表ID
     */
    private Long scaleId;
    
    /**
     * 量表名称
     */
    private String scaleName;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 结果等级
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
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 维度分析（可选）
     */
    private List<DimensionScore> dimensions;
    
    @Data
    public static class DimensionScore {
        private String dimension;
        private Integer score;
        private Integer count;
        private Double average;
    }
}
