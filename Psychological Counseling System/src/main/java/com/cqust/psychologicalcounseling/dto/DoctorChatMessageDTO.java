package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医生端聊天消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorChatMessageDTO {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 角色 student/doctor
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private String createdAt;
}
