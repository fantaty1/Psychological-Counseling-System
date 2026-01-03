package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.ApiResponse;
import com.cqust.psychologicalcounseling.dto.AdminDTO;
import com.cqust.psychologicalcounseling.dto.AnnouncementDTO;
import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.UserDTO;
import com.cqust.psychologicalcounseling.dto.request.AdminLoginRequest;
import com.cqust.psychologicalcounseling.dto.request.AnnouncementRequest;
import com.cqust.psychologicalcounseling.dto.response.AdminLoginResponse;
import com.cqust.psychologicalcounseling.service.AdminService;
import com.cqust.psychologicalcounseling.service.AnnouncementService;
import com.cqust.psychologicalcounseling.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 处理管理员相关请求
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    private final AnnouncementService announcementService;
    private final JwtUtil jwtUtil;
    
    // ==================== 认证相关 ====================
    
    /**
     * 管理员登录（隐藏入口）
     * POST /api/admin/auth/login
     */
    @PostMapping("/auth/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        log.info("管理员登录请求，用户名: {}", request.getUsername());
        AdminLoginResponse response = adminService.login(request);
        return ApiResponse.success(response, "登录成功");
    }
    
    /**
     * 获取当前管理员信息
     * GET /api/admin/profile
     */
    @GetMapping("/profile")
    public ApiResponse<AdminDTO> getProfile(HttpServletRequest request) {
        Long adminId = getAdminIdFromRequest(request);
        AdminDTO adminDTO = adminService.getAdminInfo(adminId);
        return ApiResponse.success(adminDTO, "获取成功");
    }
    
    // ==================== 学生管理 ====================
    
    /**
     * 获取所有学生
     * GET /api/admin/students
     */
    @GetMapping("/students")
    public ApiResponse<List<UserDTO>> getAllStudents() {
        log.info("获取所有学生");
        List<UserDTO> students = adminService.getAllStudents();
        return ApiResponse.success(students, "获取成功");
    }
    
    /**
     * 搜索学生
     * GET /api/admin/students/search?keyword=xxx
     */
    @GetMapping("/students/search")
    public ApiResponse<List<UserDTO>> searchStudents(@RequestParam(required = false) String keyword) {
        log.info("搜索学生，关键词: {}", keyword);
        List<UserDTO> students = adminService.searchStudents(keyword);
        return ApiResponse.success(students, "搜索成功");
    }
    
    /**
     * 更新学生状态
     * PUT /api/admin/students/{id}/status
     */
    @PutMapping("/students/{id}/status")
    public ApiResponse<Void> updateStudentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("更新学生状态，ID: {}, 状态: {}", id, status);
        adminService.updateStudentStatus(id, status);
        return ApiResponse.success("更新成功");
    }
    
    /**
     * 删除学生
     * DELETE /api/admin/students/{id}
     */
    @DeleteMapping("/students/{id}")
    public ApiResponse<Void> deleteStudent(@PathVariable Long id) {
        log.info("删除学生，ID: {}", id);
        adminService.deleteStudent(id);
        return ApiResponse.success("删除成功");
    }
    
    // ==================== 医生管理 ====================
    
    /**
     * 获取所有医生
     * GET /api/admin/doctors
     */
    @GetMapping("/doctors")
    public ApiResponse<List<DoctorDTO>> getAllDoctors() {
        log.info("获取所有医生");
        List<DoctorDTO> doctors = adminService.getAllDoctors();
        return ApiResponse.success(doctors, "获取成功");
    }
    
    /**
     * 搜索医生
     * GET /api/admin/doctors/search?keyword=xxx
     */
    @GetMapping("/doctors/search")
    public ApiResponse<List<DoctorDTO>> searchDoctors(@RequestParam(required = false) String keyword) {
        log.info("搜索医生，关键词: {}", keyword);
        List<DoctorDTO> doctors = adminService.searchDoctors(keyword);
        return ApiResponse.success(doctors, "搜索成功");
    }
    
    /**
     * 更新医生状态
     * PUT /api/admin/doctors/{id}/status
     */
    @PutMapping("/doctors/{id}/status")
    public ApiResponse<Void> updateDoctorStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("更新医生状态，ID: {}, 状态: {}", id, status);
        adminService.updateDoctorStatus(id, status);
        return ApiResponse.success("更新成功");
    }
    
    /**
     * 删除医生
     * DELETE /api/admin/doctors/{id}
     */
    @DeleteMapping("/doctors/{id}")
    public ApiResponse<Void> deleteDoctor(@PathVariable Long id) {
        log.info("删除医生，ID: {}", id);
        adminService.deleteDoctor(id);
        return ApiResponse.success("删除成功");
    }
    
    // ==================== 公告管理 ====================
    
    /**
     * 获取所有公告
     * GET /api/admin/announcements
     */
    @GetMapping("/announcements")
    public ApiResponse<List<AnnouncementDTO>> getAllAnnouncements() {
        log.info("获取所有公告");
        List<AnnouncementDTO> announcements = announcementService.getAllAnnouncements();
        return ApiResponse.success(announcements, "获取成功");
    }
    
    /**
     * 获取公告详情
     * GET /api/admin/announcements/{id}
     */
    @GetMapping("/announcements/{id}")
    public ApiResponse<AnnouncementDTO> getAnnouncement(@PathVariable Long id) {
        log.info("获取公告详情，ID: {}", id);
        AnnouncementDTO announcement = announcementService.getAnnouncementById(id);
        return ApiResponse.success(announcement, "获取成功");
    }
    
    /**
     * 创建公告
     * POST /api/admin/announcements
     */
    @PostMapping("/announcements")
    public ApiResponse<AnnouncementDTO> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        log.info("创建公告: {}", request.getTitle());
        AnnouncementDTO announcement = announcementService.createAnnouncement(request);
        return ApiResponse.success(announcement, "创建成功");
    }
    
    /**
     * 更新公告
     * PUT /api/admin/announcements/{id}
     */
    @PutMapping("/announcements/{id}")
    public ApiResponse<AnnouncementDTO> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        request.setId(id);
        log.info("更新公告，ID: {}", id);
        AnnouncementDTO announcement = announcementService.updateAnnouncement(request);
        return ApiResponse.success(announcement, "更新成功");
    }
    
    /**
     * 更新公告状态
     * PUT /api/admin/announcements/{id}/status
     */
    @PutMapping("/announcements/{id}/status")
    public ApiResponse<Void> updateAnnouncementStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("更新公告状态，ID: {}, 状态: {}", id, status);
        announcementService.updateAnnouncementStatus(id, status);
        return ApiResponse.success("更新成功");
    }
    
    /**
     * 删除公告
     * DELETE /api/admin/announcements/{id}
     */
    @DeleteMapping("/announcements/{id}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        log.info("删除公告，ID: {}", id);
        announcementService.deleteAnnouncement(id);
        return ApiResponse.success("删除成功");
    }
    
    // ==================== 公共接口（学生端使用） ====================
    
    /**
     * 获取已发布的公告（学生端使用）
     * GET /api/announcements
     */
    @GetMapping("/public/announcements")
    public ApiResponse<List<AnnouncementDTO>> getPublishedAnnouncements(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        log.info("获取已发布的公告，数量限制: {}", limit);
        List<AnnouncementDTO> announcements = announcementService.getPublishedAnnouncements(limit);
        return ApiResponse.success(announcements, "获取成功");
    }
    
    /**
     * 从请求中获取管理员ID
     */
    private Long getAdminIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未授权");
    }
}
