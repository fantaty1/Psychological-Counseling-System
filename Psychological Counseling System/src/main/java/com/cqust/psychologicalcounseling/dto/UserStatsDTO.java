package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    
    /**
     * 累计咨询时长（小时）
     */
    private Double counselingHours;
    
    /**
     * 累计预约次数
     */
    private Integer appointmentsCount;
    
    /**
     * 连续签到天数
     */
    private Integer checkinStreak;
}
