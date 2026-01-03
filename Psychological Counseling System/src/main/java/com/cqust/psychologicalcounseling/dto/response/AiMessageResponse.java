package com.cqust.psychologicalcounseling.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI消息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMessageResponse {
    
    /**
     * AI回复内容
     */
    private String reply;
    
    /**
     * 风险等级：low/medium/high
     */
    private String riskLevel;
    
    /**
     * 建议的操作
     */
    private List<String> suggestedActions;
}
