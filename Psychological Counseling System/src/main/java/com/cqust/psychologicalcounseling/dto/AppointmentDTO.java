package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预约信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    
    /**
     * 预约ID
     */
    private Long id;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 学生学号
     */
    private String studentIdNo;
    
    /**
     * 学生头像
     */
    private String studentAvatar;
    
    /**
     * 学生学院
     */
    private String studentCollege;
    
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
     * 预约日期
     */
    private String date;
    
    /**
     * 时间段
     */
    private String timeSlot;
    
    /**
     * 咨询方式 offline/online
     */
    private String type;
    
    /**
     * 状态 pending/confirmed/completed/rejected/cancelled
     */
    private String status;
    
    /**
     * 咨询描述/备注
     */
    private String description;
    
    /**
     * 咨询地点
     */
    private String location;
    
    /**
     * 创建时间
     */
    private String createdAt;
}
