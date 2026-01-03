package com.cqust.psychologicalcounseling.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 更新排班请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {
    
    /**
     * 排班数据
     * Map<日期, Map<时间段, 是否可预约>>
     */
    private Map<String, Map<String, Boolean>> schedules;
}
