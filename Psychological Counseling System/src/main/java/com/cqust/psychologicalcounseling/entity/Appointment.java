package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约实体类
 * 对应数据库表：appointment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    /**
     * 预约ID，主键
     */
    private Long id;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 咨询师ID
     */
    private Long doctorId;
    
    /**
     * 预约日期
     */
    private LocalDate appointmentDate;
    
    /**
     * 时间段（如：14:00）
     */
    private String timeSlot;
    
    /**
     * 咨询方式：offline-线下 online-线上
     */
    private String type;
    
    /**
     * 状态：pending-待确认 confirmed-已确认 completed-已完成 rejected-已拒绝 cancelled-已取消
     */
    private String status;
    
    /**
     * 学生预约描述
     */
    private String description;
    
    /**
     * 咨询地点
     */
    private String location;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 咨询备注（完成后填写）
     */
    private String notes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 删除时间（软删除）
     */
    private LocalDateTime deletedAt;
}
