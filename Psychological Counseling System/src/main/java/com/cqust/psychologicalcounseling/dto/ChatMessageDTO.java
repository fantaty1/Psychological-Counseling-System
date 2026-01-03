package com.cqust.psychologicalcounseling.dto;

import com.cqust.psychologicalcounseling.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    /**
     * 消息ID
     */
    private Long id;
    
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
    
    /**
     * 从实体转换为DTO
     *
     * @param message 消息实体
     * @return ChatMessageDTO
     */
    public static ChatMessageDTO fromEntity(ChatMessage message) {
        if (message == null) {
            return null;
        }
        
        return ChatMessageDTO.builder()
                .id(message.getId())
                .role(message.getRole())
                .content(message.getContent())
                .type(message.getType())
                .riskLevel(message.getRiskLevel())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
