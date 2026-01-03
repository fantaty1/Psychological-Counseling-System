package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 更新排班请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {
    
    /**
     * 排班数据
     * 格式：{ "2025-12-20": { "09:00": true, "10:00": false, ... }, ... }
     * key: 日期字符串
     * value: 时间段与是否可用的映射
     */
    private Map<String, Map<String, Boolean>> schedules;
}
