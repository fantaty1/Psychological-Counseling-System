package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 咨询师排班实体类
 * 对应数据库表：doctor_schedule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSchedule {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 咨询师ID
     */
    private Long doctorId;
    
    /**
     * 日期
     */
    private LocalDate scheduleDate;
    
    /**
     * 时间段（如：09:00, 10:00, 14:00）
     */
    private String timeSlot;
    
    /**
     * 是否可预约：0-否 1-是
     */
    private Integer available;
    
    /**
     * 是否已被预约：0-否 1-是
     */
    private Integer booked;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
