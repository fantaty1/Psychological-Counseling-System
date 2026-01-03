package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.*;

import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {
    
    /**
     * 获取咨询师今日预约列表
     * @param doctorId 咨询师ID
     * @return 今日预约列表
     */
    List<AppointmentDTO> getTodayAppointments(Long doctorId);
    
    /**
     * 获取咨询师预约列表（分页）
     * @param doctorId 咨询师ID
     * @param status 状态筛选
     * @param page 页码
     * @param pageSize 每页数量
     * @return 预约列表
     */
    AppointmentListDTO getAppointments(Long doctorId, String status, int page, int pageSize);
    
    /**
     * 确认预约
     * @param appointmentId 预约ID
     * @param doctorId 咨询师ID
     * @param location 咨询地点
     * @return 更新后的预约信息
     */
    AppointmentDTO confirmAppointment(Long appointmentId, Long doctorId, String location);
    
    /**
     * 拒绝预约
     * @param appointmentId 预约ID
     * @param doctorId 咨询师ID
     * @param reason 拒绝原因
     * @return 更新后的预约信息
     */
    AppointmentDTO rejectAppointment(Long appointmentId, Long doctorId, String reason);
    
    /**
     * 完成预约
     * @param appointmentId 预约ID
     * @param doctorId 咨询师ID
     * @param notes 咨询备注
     * @return 更新后的预约信息
     */
    AppointmentDTO completeAppointment(Long appointmentId, Long doctorId, String notes);
    
    /**
     * 学生创建预约
     * @param request 预约请求
     * @param studentId 学生ID
     * @return 预约信息
     */
    AppointmentDTO createAppointment(CreateAppointmentRequest request, Long studentId);
    
    /**
     * 获取学生的所有预约
     * @param studentId 学生ID
     * @return 预约列表
     */
    List<AppointmentDTO> getStudentAppointments(Long studentId);
    
    /**
     * 学生取消预约
     * @param appointmentId 预约ID
     * @param studentId 学生ID
     * @return 更新后的预约信息
     */
    AppointmentDTO cancelAppointment(Long appointmentId, Long studentId);
    
    /**
     * 根据ID获取预约详情
     * @param appointmentId 预约ID
     * @return 预约信息
     */
    AppointmentDTO getAppointmentById(Long appointmentId);
}
