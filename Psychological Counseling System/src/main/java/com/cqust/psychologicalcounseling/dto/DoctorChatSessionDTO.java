package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医生端聊天会话DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorChatSessionDTO {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 学生头像
     */
    private String studentAvatar;
    
    /**
     * 学生学院
     */
    private String college;
    
    /**
     * 最后一条消息
     */
    private String lastMessage;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
    
    /**
     * 更新时间
     */
    private String updatedAt;
    
    // ====== 学生端需要的医生信息 ======
    
    /**
     * 医生ID
     */
    private Long doctorId;
    
    /**
     * 医生姓名
     */
    private String doctorName;
    
    /**
     * 医生头像
     */
    private String doctorAvatar;
    
    /**
     * 医生职称
     */
    private String doctorTitle;
}
