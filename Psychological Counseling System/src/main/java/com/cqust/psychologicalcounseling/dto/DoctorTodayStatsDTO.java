package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 咨询师今日统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorTodayStatsDTO {
    
    /**
     * 今日预约数
     */
    private Integer todayAppointments;
    
    /**
     * 已完成数
     */
    private Integer completed;
    
    /**
     * 待确认数
     */
    private Integer pending;
    
    /**
     * 未读消息数
     */
    private Integer unreadMessages;
}
