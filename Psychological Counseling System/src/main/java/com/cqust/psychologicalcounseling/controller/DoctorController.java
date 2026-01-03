package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.ApiResponse;
import com.cqust.psychologicalcounseling.dto.*;
import com.cqust.psychologicalcounseling.service.AppointmentService;
import com.cqust.psychologicalcounseling.service.DoctorChatService;
import com.cqust.psychologicalcounseling.service.DoctorScheduleService;
import com.cqust.psychologicalcounseling.service.DoctorService;
import com.cqust.psychologicalcounseling.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 咨询师端控制器
 * 处理咨询师相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {
    
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DoctorChatService doctorChatService;
    private final DoctorScheduleService doctorScheduleService;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取当前登录咨询师ID
     */
    private Long getCurrentDoctorId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
    
    // ==================== 个人信息相关接口 ====================
    
    /**
     * 获取咨询师个人信息
     * GET /api/doctor/profile
     */
    @GetMapping("/profile")
    public ApiResponse<DoctorDTO> getProfile(HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取咨询师个人信息，咨询师ID: {}", doctorId);
        DoctorDTO profile = doctorService.getDoctorProfile(doctorId);
        return ApiResponse.success(profile);
    }
    
    /**
     * 更新咨询师个人信息
     * PUT /api/doctor/profile
     */
    @PutMapping("/profile")
    public ApiResponse<DoctorDTO> updateProfile(
            @Valid @RequestBody UpdateDoctorProfileRequest updateRequest,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("更新咨询师个人信息，咨询师ID: {}", doctorId);
        DoctorDTO profile = doctorService.updateDoctorProfile(doctorId, updateRequest);
        return ApiResponse.success(profile);
    }
    
    // ==================== 统计相关接口 ====================
    
    /**
     * 获取今日统计
     * GET /api/doctor/stats/today
     */
    @GetMapping("/stats/today")
    public ApiResponse<DoctorTodayStatsDTO> getTodayStats(HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取咨询师今日统计，咨询师ID: {}", doctorId);
        DoctorTodayStatsDTO stats = doctorService.getTodayStats(doctorId);
        return ApiResponse.success(stats);
    }
    
    // ==================== 预约相关接口 ====================
    
    /**
     * 获取今日预约列表
     * GET /api/doctor/appointments/today
     */
    @GetMapping("/appointments/today")
    public ApiResponse<List<AppointmentDTO>> getTodayAppointments(HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取咨询师今日预约，咨询师ID: {}", doctorId);
        List<AppointmentDTO> appointments = appointmentService.getTodayAppointments(doctorId);
        return ApiResponse.success(appointments);
    }
    
    /**
     * 获取预约列表（分页）
     * GET /api/doctor/appointments
     */
    @GetMapping("/appointments")
    public ApiResponse<AppointmentListDTO> getAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取咨询师预约列表，咨询师ID: {}, 状态: {}, 页码: {}", doctorId, status, page);
        AppointmentListDTO appointments = appointmentService.getAppointments(doctorId, status, page, pageSize);
        return ApiResponse.success(appointments);
    }
    
    /**
     * 确认预约
     * PUT /api/doctor/appointments/{id}/confirm
     */
    @PutMapping("/appointments/{id}/confirm")
    public ApiResponse<AppointmentDTO> confirmAppointment(
            @PathVariable("id") Long appointmentId,
            @RequestBody(required = false) ConfirmAppointmentRequest confirmRequest,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        String location = confirmRequest != null ? confirmRequest.getLocation() : null;
        log.info("确认预约，咨询师ID: {}, 预约ID: {}", doctorId, appointmentId);
        AppointmentDTO appointment = appointmentService.confirmAppointment(appointmentId, doctorId, location);
        return ApiResponse.success(appointment);
    }
    
    /**
     * 拒绝预约
     * PUT /api/doctor/appointments/{id}/reject
     */
    @PutMapping("/appointments/{id}/reject")
    public ApiResponse<AppointmentDTO> rejectAppointment(
            @PathVariable("id") Long appointmentId,
            @Valid @RequestBody RejectAppointmentRequest rejectRequest,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("拒绝预约，咨询师ID: {}, 预约ID: {}", doctorId, appointmentId);
        AppointmentDTO appointment = appointmentService.rejectAppointment(appointmentId, doctorId, rejectRequest.getReason());
        return ApiResponse.success(appointment);
    }
    
    /**
     * 完成预约
     * PUT /api/doctor/appointments/{id}/complete
     */
    @PutMapping("/appointments/{id}/complete")
    public ApiResponse<AppointmentDTO> completeAppointment(
            @PathVariable("id") Long appointmentId,
            @RequestBody(required = false) CompleteAppointmentRequest completeRequest,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        String notes = completeRequest != null ? completeRequest.getNotes() : null;
        log.info("完成预约，咨询师ID: {}, 预约ID: {}", doctorId, appointmentId);
        AppointmentDTO appointment = appointmentService.completeAppointment(appointmentId, doctorId, notes);
        return ApiResponse.success(appointment);
    }
    
    // ==================== 消息相关接口 ====================
    
    /**
     * 获取待回复消息列表
     * GET /api/doctor/messages/pending
     */
    @GetMapping("/messages/pending")
    public ApiResponse<List<DoctorChatMessageDTO>> getPendingMessages(HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取待回复消息，咨询师ID: {}", doctorId);
        List<DoctorChatMessageDTO> messages = doctorChatService.getPendingMessages(doctorId);
        return ApiResponse.success(messages);
    }
    
    /**
     * 获取聊天会话列表
     * GET /api/doctor/chat/sessions
     */
    @GetMapping("/chat/sessions")
    public ApiResponse<List<DoctorChatSessionDTO>> getChatSessions(HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取聊天会话列表，咨询师ID: {}", doctorId);
        List<DoctorChatSessionDTO> sessions = doctorChatService.getSessions(doctorId);
        return ApiResponse.success(sessions);
    }
    
    /**
     * 获取会话消息记录
     * GET /api/doctor/chat/sessions/{session_id}/messages
     */
    @GetMapping("/chat/sessions/{session_id}/messages")
    public ApiResponse<SessionMessagesDTO> getSessionMessages(
            @PathVariable("session_id") String sessionId,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取会话消息，咨询师ID: {}, 会话ID: {}", doctorId, sessionId);
        SessionMessagesDTO messages = doctorChatService.getSessionMessages(sessionId, doctorId);
        return ApiResponse.success(messages);
    }
    
    /**
     * 发送消息
     * POST /api/doctor/chat/send
     */
    @PostMapping("/chat/send")
    public ApiResponse<DoctorChatMessageDTO> sendMessage(
            @Valid @RequestBody DoctorSendMessageRequest sendRequest,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("咨询师发送消息，咨询师ID: {}, 会话ID: {}", doctorId, sendRequest.getSessionId());
        DoctorChatMessageDTO message = doctorChatService.sendMessage(sendRequest, doctorId);
        return ApiResponse.success(message);
    }
    
    // ==================== 排班相关接口 ====================
    
    /**
     * 获取排班
     * GET /api/doctor/schedules
     */
    @GetMapping("/schedules")
    public ApiResponse<ScheduleDTO> getSchedules(
            @RequestParam(defaultValue = "0") int weekOffset,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("获取咨询师排班，咨询师ID: {}, 周偏移: {}", doctorId, weekOffset);
        ScheduleDTO schedule = doctorScheduleService.getWeeklySchedule(doctorId, weekOffset);
        return ApiResponse.success(schedule);
    }
    
    /**
     * 更新排班
     * PUT /api/doctor/schedules
     */
    @PutMapping("/schedules")
    public ApiResponse<ScheduleDTO> updateSchedules(
            @Valid @RequestBody UpdateScheduleRequest updateRequest,
            HttpServletRequest request) {
        Long doctorId = getCurrentDoctorId(request);
        log.info("更新咨询师排班，咨询师ID: {}", doctorId);
        ScheduleDTO schedule = doctorScheduleService.updateSchedule(doctorId, updateRequest);
        return ApiResponse.success(schedule);
    }
}
