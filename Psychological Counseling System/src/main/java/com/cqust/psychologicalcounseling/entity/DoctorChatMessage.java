package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 医生-学生聊天消息实体类
 * 对应数据库表：doctor_chat_message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorChatMessage {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 发送者角色：student-学生 doctor-咨询师
     */
    private String role;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
