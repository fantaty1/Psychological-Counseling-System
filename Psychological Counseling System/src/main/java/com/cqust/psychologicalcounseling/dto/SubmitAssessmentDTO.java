package com.cqust.psychologicalcounseling.dto;

import lombok.Data;
import java.util.List;

/**
 * 提交测评答案请求DTO
 */
@Data
public class SubmitAssessmentDTO {
    /**
     * 量表ID
     */
    private Long scaleId;
    
    /**
     * 答题用时（秒）
     */
    private Integer duration;
    
    /**
     * 答案列表
     */
    private List<AnswerItem> answers;
    
    @Data
    public static class AnswerItem {
        /**
         * 题目ID
         */
        private Long questionId;
        
        /**
         * 选择的答案值
         */
        private Integer answerValue;
    }
}
