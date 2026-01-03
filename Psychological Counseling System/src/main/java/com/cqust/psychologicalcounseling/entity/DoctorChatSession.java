package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 医生-学生聊天会话实体类
 * 对应数据库表：doctor_chat_session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorChatSession {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会话ID（UUID）
     */
    private String sessionId;
    
    /**
     * 咨询师ID
     */
    private Long doctorId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 医生未读消息数
     */
    private Integer doctorUnreadCount;
    
    /**
     * 学生未读消息数
     */
    private Integer studentUnreadCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
