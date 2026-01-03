package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.ScheduleDTO;
import com.cqust.psychologicalcounseling.dto.UpdateScheduleRequest;
import com.cqust.psychologicalcounseling.entity.DoctorSchedule;
import com.cqust.psychologicalcounseling.mapper.DoctorScheduleMapper;
import com.cqust.psychologicalcounseling.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 咨询师排班服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {
    
    private final DoctorScheduleMapper doctorScheduleMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public ScheduleDTO getWeeklySchedule(Long doctorId, int weekOffset) {
        // 计算周起始和结束日期
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(weekOffset);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        // 查询排班数据
        List<DoctorSchedule> schedules = doctorScheduleMapper.findByDoctorIdAndDateRange(doctorId, weekStart, weekEnd);
        
        // 构建返回数据
        Map<String, Map<String, ScheduleDTO.SlotStatus>> scheduleMap = new LinkedHashMap<>();
        
        // 初始化每天的时间段
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            String dateStr = date.format(DATE_FORMATTER);
            scheduleMap.put(dateStr, new LinkedHashMap<>());
        }
        
        // 填充排班数据
        for (DoctorSchedule schedule : schedules) {
            String dateStr = schedule.getScheduleDate().format(DATE_FORMATTER);
            Map<String, ScheduleDTO.SlotStatus> daySlots = scheduleMap.get(dateStr);
            if (daySlots != null) {
                ScheduleDTO.SlotStatus status = ScheduleDTO.SlotStatus.builder()
                        .available(schedule.getAvailable() == 1)
                        .booked(schedule.getBooked() == 1)
                        .build();
                daySlots.put(schedule.getTimeSlot(), status);
            }
        }
        
        return ScheduleDTO.builder()
                .weekStart(weekStart.format(DATE_FORMATTER))
                .weekEnd(weekEnd.format(DATE_FORMATTER))
                .schedules(scheduleMap)
                .build();
    }
    
    @Override
    @Transactional
    public ScheduleDTO updateSchedule(Long doctorId, UpdateScheduleRequest request) {
        Map<String, Map<String, Boolean>> schedules = request.getSchedules();
        
        if (schedules == null || schedules.isEmpty()) {
            return getWeeklySchedule(doctorId, 0);
        }
        
        // 找出日期范围
        List<LocalDate> dates = new ArrayList<>();
        for (String dateStr : schedules.keySet()) {
            dates.add(LocalDate.parse(dateStr, DATE_FORMATTER));
        }
        Collections.sort(dates);
        LocalDate startDate = dates.get(0);
        LocalDate endDate = dates.get(dates.size() - 1);
        
        // 获取已有的预约状态（保留已被预约的时间段）
        List<DoctorSchedule> existingSchedules = doctorScheduleMapper.findByDoctorIdAndDateRange(doctorId, startDate, endDate);
        Map<String, Map<String, Integer>> bookedMap = new HashMap<>();
        for (DoctorSchedule s : existingSchedules) {
            String dateStr = s.getScheduleDate().format(DATE_FORMATTER);
            bookedMap.computeIfAbsent(dateStr, k -> new HashMap<>()).put(s.getTimeSlot(), s.getBooked());
        }
        
        // 删除旧排班
        doctorScheduleMapper.deleteByDoctorIdAndDateRange(doctorId, startDate, endDate);
        
        // 批量插入新排班
        List<DoctorSchedule> newSchedules = new ArrayList<>();
        for (Map.Entry<String, Map<String, Boolean>> dayEntry : schedules.entrySet()) {
            String dateStr = dayEntry.getKey();
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            Map<String, Boolean> slots = dayEntry.getValue();
            
            for (Map.Entry<String, Boolean> slotEntry : slots.entrySet()) {
                String timeSlot = slotEntry.getKey();
                boolean available = slotEntry.getValue();
                
                // 检查是否已被预约
                int booked = 0;
                if (bookedMap.containsKey(dateStr) && bookedMap.get(dateStr).containsKey(timeSlot)) {
                    booked = bookedMap.get(dateStr).get(timeSlot);
                }
                
                DoctorSchedule schedule = DoctorSchedule.builder()
                        .doctorId(doctorId)
                        .scheduleDate(date)
                        .timeSlot(timeSlot)
                        .available(available ? 1 : 0)
                        .booked(booked)
                        .build();
                newSchedules.add(schedule);
            }
        }
        
        if (!newSchedules.isEmpty()) {
            doctorScheduleMapper.batchInsert(newSchedules);
        }
        
        return getWeeklySchedule(doctorId, 0);
    }
    
    @Override
    public boolean isSlotAvailable(Long doctorId, String date, String timeSlot) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        DoctorSchedule schedule = doctorScheduleMapper.checkSlotAvailable(doctorId, localDate, timeSlot);
        return schedule != null;
    }
    
    @Override
    public void markSlotAsBooked(Long doctorId, String date, String timeSlot) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        doctorScheduleMapper.updateBooked(doctorId, localDate, timeSlot, 1);
    }
    
    @Override
    public void markSlotAsAvailable(Long doctorId, String date, String timeSlot) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        doctorScheduleMapper.updateBooked(doctorId, localDate, timeSlot, 0);
    }
}
