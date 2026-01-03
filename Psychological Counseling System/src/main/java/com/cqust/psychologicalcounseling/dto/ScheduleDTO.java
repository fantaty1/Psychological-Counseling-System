package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 咨询师排班DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    
    /**
     * 本周开始日期
     */
    private String weekStart;
    
    /**
     * 本周结束日期
     */
    private String weekEnd;
    
    /**
     * 排班数据
     * Map<日期, Map<时间段, 时间段状态>>
     */
    private Map<String, Map<String, SlotStatus>> schedules;
    
    /**
     * 时间段状态
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotStatus {
        /**
         * 是否可预约
         */
        private Boolean available;
        
        /**
         * 是否已被预约
         */
        private Boolean booked;
    }
}
