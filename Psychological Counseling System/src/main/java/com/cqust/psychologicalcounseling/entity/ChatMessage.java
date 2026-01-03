package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI聊天消息实体类
 * 对应数据库表：chat_message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    /**
     * 消息ID，主键
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 角色：user-用户 assistant-AI
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：text/image/card
     */
    private String type;
    
    /**
     * 风险等级：low/medium/high
     */
    private String riskLevel;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
