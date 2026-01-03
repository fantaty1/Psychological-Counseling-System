package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI会话实体类
 * 对应数据库表：chat_session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    
    /**
     * 主键
     */
    private Long id;
    
    /**
     * 会话UUID（唯一）
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 状态：0-已删除 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
