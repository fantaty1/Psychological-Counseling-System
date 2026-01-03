package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.ApiResponse;
import com.cqust.psychologicalcounseling.dto.AppointmentDTO;
import com.cqust.psychologicalcounseling.dto.CreateAppointmentRequest;
import com.cqust.psychologicalcounseling.dto.DoctorChatMessageDTO;
import com.cqust.psychologicalcounseling.dto.DoctorChatSessionDTO;
import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.ScheduleDTO;
import com.cqust.psychologicalcounseling.dto.SessionMessagesDTO;
import com.cqust.psychologicalcounseling.dto.UserDTO;
import com.cqust.psychologicalcounseling.dto.request.UpdateProfileRequest;
import com.cqust.psychologicalcounseling.service.AppointmentService;
import com.cqust.psychologicalcounseling.service.DoctorChatService;
import com.cqust.psychologicalcounseling.service.DoctorScheduleService;
import com.cqust.psychologicalcounseling.service.DoctorService;
import com.cqust.psychologicalcounseling.service.UserService;
import com.cqust.psychologicalcounseling.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户个人信息相关请求
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DoctorScheduleService doctorScheduleService;
    private final DoctorChatService doctorChatService;
    private final UserContext userContext;
    
    /**
     * 获取当前用户个人信息
     *
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile(HttpServletRequest request) {
        Long userId = userContext.getCurrentUserId(request);
        log.info("获取用户信息，用户ID: {}", userId);
        UserDTO userDTO = userService.getProfile(userId);
        return ApiResponse.success(userDTO, "获取成功");
    }
    
    /**
     * 更新用户个人信息
     *
     * @param request       HTTP请求
     * @param updateRequest 更新请求
     * @return 更新结果
     */
    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(HttpServletRequest request,
                                           @Valid @RequestBody UpdateProfileRequest updateRequest) {
        Long userId = userContext.getCurrentUserId(request);
        log.info("更新用户信息，用户ID: {}", userId);
        userService.updateProfile(userId, updateRequest);
        return ApiResponse.success("更新成功");
    }
    
    /**
     * 修改密码
     *
     * @param request HTTP请求
     * @param body    包含oldPassword和newPassword
     * @return 修改结果
     */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(HttpServletRequest request,
                                            @RequestBody Map<String, String> body) {
        Long userId = userContext.getCurrentUserId(request);
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        log.info("用户修改密码，用户ID: {}", userId);
        userService.changePassword(userId, oldPassword, newPassword);
        return ApiResponse.success("密码修改成功");
    }
    
    // ==================== 医生相关接口 ====================
    
    /**
     * 获取所有可用医生列表
     * GET /api/user/doctors
     */
    @GetMapping("/doctors")
    public ApiResponse<List<DoctorDTO>> getAllDoctors() {
        log.info("获取医生列表");
        List<DoctorDTO> doctors = doctorService.getAllAvailableDoctors();
        return ApiResponse.success(doctors, "获取成功");
    }
    
    /**
     * 搜索医生
     * GET /api/user/doctors/search?keyword=xxx
     */
    @GetMapping("/doctors/search")
    public ApiResponse<List<DoctorDTO>> searchDoctors(@RequestParam(required = false) String keyword) {
        log.info("搜索医生，关键词: {}", keyword);
        List<DoctorDTO> doctors = doctorService.searchDoctors(keyword);
        return ApiResponse.success(doctors, "搜索成功");
    }
    
    /**
     * 获取医生详情
     * GET /api/user/doctors/{id}
     */
    @GetMapping("/doctors/{id}")
    public ApiResponse<DoctorDTO> getDoctorDetail(@PathVariable Long id) {
        log.info("获取医生详情，医生ID: {}", id);
        DoctorDTO doctor = doctorService.getDoctorProfile(id);
        return ApiResponse.success(doctor, "获取成功");
    }
    
    /**
     * 获取医生的可用排班（特定周）
     * GET /api/user/doctors/{doctorId}/schedule?weekOffset=0
     */
    @GetMapping("/doctors/{doctorId}/schedule")
    public ApiResponse<ScheduleDTO> getDoctorSchedule(
            @PathVariable Long doctorId,
            @RequestParam(required = false, defaultValue = "0") int weekOffset) {
        log.info("获取医生排班，医生ID: {}, 周偏移: {}", doctorId, weekOffset);
        ScheduleDTO schedule = doctorScheduleService.getWeeklySchedule(doctorId, weekOffset);
        return ApiResponse.success(schedule, "获取成功");
    }
    
    // ==================== 预约相关接口 ====================
    
    /**
     * 创建预约
     * POST /api/user/appointments
     */
    @PostMapping("/appointments")
    public ApiResponse<AppointmentDTO> createAppointment(
            HttpServletRequest request,
            @Valid @RequestBody CreateAppointmentRequest appointmentRequest) {
        Long studentId = userContext.getCurrentUserId(request);
        log.info("学生创建预约，学生ID: {}, 医生ID: {}", studentId, appointmentRequest.getDoctorId());
        AppointmentDTO appointment = appointmentService.createAppointment(appointmentRequest, studentId);
        return ApiResponse.success(appointment, "预约创建成功");
    }
    
    /**
     * 获取当前学生的所有预约
     * GET /api/user/appointments
     */
    @GetMapping("/appointments")
    public ApiResponse<List<AppointmentDTO>> getMyAppointments(HttpServletRequest request) {
        Long studentId = userContext.getCurrentUserId(request);
        log.info("获取学生预约列表，学生ID: {}", studentId);
        List<AppointmentDTO> appointments = appointmentService.getStudentAppointments(studentId);
        return ApiResponse.success(appointments, "获取成功");
    }
    
    /**
     * 取消预约
     * PUT /api/user/appointments/{id}/cancel
     */
    @PutMapping("/appointments/{id}/cancel")
    public ApiResponse<AppointmentDTO> cancelAppointment(
            HttpServletRequest request,
            @PathVariable Long id) {
        Long studentId = userContext.getCurrentUserId(request);
        log.info("学生取消预约，学生ID: {}, 预约ID: {}", studentId, id);
        AppointmentDTO appointment = appointmentService.cancelAppointment(id, studentId);
        return ApiResponse.success(appointment, "预约已取消");
    }
    
    // ==================== 聊天相关接口 ====================
    
    /**
     * 获取或创建与医生的聊天会话
     * POST /api/user/chat/sessions
     */
    @PostMapping("/chat/sessions")
    public ApiResponse<Map<String, String>> getOrCreateChatSession(
            HttpServletRequest request,
            @RequestBody Map<String, Long> body) {
        Long studentId = userContext.getCurrentUserId(request);
        Long doctorId = body.get("doctorId");
        log.info("创建聊天会话，学生ID: {}, 医生ID: {}", studentId, doctorId);
        String sessionId = doctorChatService.getOrCreateSession(doctorId, studentId);
        return ApiResponse.success(Map.of("sessionId", sessionId), "获取成功");
    }
    
    /**
     * 获取学生的聊天会话列表
     * GET /api/user/chat/sessions
     */
    @GetMapping("/chat/sessions")
    public ApiResponse<List<DoctorChatSessionDTO>> getChatSessions(HttpServletRequest request) {
        Long studentId = userContext.getCurrentUserId(request);
        log.info("获取学生聊天会话列表，学生ID: {}", studentId);
        List<DoctorChatSessionDTO> sessions = doctorChatService.getStudentSessions(studentId);
        return ApiResponse.success(sessions, "获取成功");
    }
    
    /**
     * 获取会话消息记录
     * GET /api/user/chat/sessions/{sessionId}/messages
     */
    @GetMapping("/chat/sessions/{sessionId}/messages")
    public ApiResponse<SessionMessagesDTO> getSessionMessages(
            @PathVariable String sessionId,
            HttpServletRequest request) {
        Long studentId = userContext.getCurrentUserId(request);
        log.info("获取会话消息，学生ID: {}, 会话ID: {}", studentId, sessionId);
        SessionMessagesDTO messages = doctorChatService.getStudentSessionMessages(sessionId, studentId);
        return ApiResponse.success(messages, "获取成功");
    }
    
    /**
     * 学生发送消息
     * POST /api/user/chat/send
     */
    @PostMapping("/chat/send")
    public ApiResponse<DoctorChatMessageDTO> sendMessage(
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        Long studentId = userContext.getCurrentUserId(request);
        String sessionId = body.get("sessionId");
        String content = body.get("content");
        log.info("学生发送消息，学生ID: {}, 会话ID: {}", studentId, sessionId);
        DoctorChatMessageDTO message = doctorChatService.studentSendMessage(sessionId, studentId, content);
        return ApiResponse.success(message, "发送成功");
    }
}
