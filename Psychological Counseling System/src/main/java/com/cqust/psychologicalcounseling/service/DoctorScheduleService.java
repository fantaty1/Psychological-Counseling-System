package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.ScheduleDTO;
import com.cqust.psychologicalcounseling.dto.UpdateScheduleRequest;

/**
 * 咨询师排班服务接口
 */
public interface DoctorScheduleService {
    
    /**
     * 获取咨询师某周的排班
     * @param doctorId 咨询师ID
     * @param weekOffset 周偏移量（0表示本周，1表示下周，-1表示上周）
     * @return 排班信息
     */
    ScheduleDTO getWeeklySchedule(Long doctorId, int weekOffset);
    
    /**
     * 更新咨询师排班
     * @param doctorId 咨询师ID
     * @param request 排班更新请求
     * @return 更新后的排班信息
     */
    ScheduleDTO updateSchedule(Long doctorId, UpdateScheduleRequest request);
    
    /**
     * 检查某时间段是否可预约
     * @param doctorId 咨询师ID
     * @param date 日期字符串
     * @param timeSlot 时间段
     * @return 是否可预约
     */
    boolean isSlotAvailable(Long doctorId, String date, String timeSlot);
    
    /**
     * 标记时间段为已预约
     * @param doctorId 咨询师ID
     * @param date 日期字符串
     * @param timeSlot 时间段
     */
    void markSlotAsBooked(Long doctorId, String date, String timeSlot);
    
    /**
     * 标记时间段为可用
     * @param doctorId 咨询师ID
     * @param date 日期字符串
     * @param timeSlot 时间段
     */
    void markSlotAsAvailable(Long doctorId, String date, String timeSlot);
}
