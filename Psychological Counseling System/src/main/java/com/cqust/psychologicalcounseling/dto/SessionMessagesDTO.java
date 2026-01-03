package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会话消息列表DTO（包含学生信息）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionMessagesDTO {
    
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
    
    /**
     * 消息列表
     */
    private List<DoctorChatMessageDTO> messages;
}
