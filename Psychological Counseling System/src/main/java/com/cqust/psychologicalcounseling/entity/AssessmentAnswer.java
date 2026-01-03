package com.cqust.psychologicalcounseling.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户答题记录实体类
 */
@Data
public class AssessmentAnswer {
    /**
     * 答案ID
     */
    private Long id;
    
    /**
     * 测评记录ID
     */
    private Long recordId;
    
    /**
     * 题目ID
     */
    private Long questionId;
    
    /**
     * 选择的答案值
     */
    private Integer answerValue;
    
    /**
     * 得分
     */
    private Integer score;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
