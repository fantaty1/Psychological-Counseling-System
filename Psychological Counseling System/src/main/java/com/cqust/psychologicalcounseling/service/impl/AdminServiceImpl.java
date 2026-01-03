package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.AdminDTO;
import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.UserDTO;
import com.cqust.psychologicalcounseling.dto.request.AdminLoginRequest;
import com.cqust.psychologicalcounseling.dto.response.AdminLoginResponse;
import com.cqust.psychologicalcounseling.entity.Admin;
import com.cqust.psychologicalcounseling.entity.Doctor;
import com.cqust.psychologicalcounseling.entity.User;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.AdminMapper;
import com.cqust.psychologicalcounseling.mapper.DoctorMapper;
import com.cqust.psychologicalcounseling.mapper.UserMapper;
import com.cqust.psychologicalcounseling.service.AdminService;
import com.cqust.psychologicalcounseling.util.JwtUtil;
import com.cqust.psychologicalcounseling.util.PasswordUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    
    /**
     * 管理员登录
     */
    @Override
    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request) {
        log.info("管理员登录，用户名: {}", request.getUsername());
        
        // 根据用户名查询管理员
        Admin admin = adminMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        
        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查状态
        if (admin.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用");
        }
        
        // 更新最后登录时间
        adminMapper.updateLastLoginTime(admin.getId(), LocalDateTime.now());
        
        // 生成token（使用admin:前缀标识管理员）
        String token = jwtUtil.generateToken(admin.getId(), "admin:" + admin.getUsername());
        
        // 构建响应
        AdminDTO adminDTO = convertToAdminDTO(admin);
        
        log.info("管理员登录成功，ID: {}", admin.getId());
        
        return AdminLoginResponse.builder()
                .token(token)
                .admin(adminDTO)
                .role("admin")
                .build();
    }
    
    /**
     * 获取管理员信息
     */
    @Override
    public AdminDTO getAdminInfo(Long adminId) {
        Admin admin = adminMapper.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        return convertToAdminDTO(admin);
    }
    
    /**
     * 获取所有学生用户
     */
    @Override
    public List<UserDTO> getAllStudents() {
        List<User> users = userMapper.findAll();
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 搜索学生用户
     */
    @Override
    public List<UserDTO> searchStudents(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllStudents();
        }
        List<User> users = userMapper.searchByKeyword(keyword);
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新学生状态
     */
    @Override
    @Transactional
    public void updateStudentStatus(Long userId, Integer status) {
        log.info("更新学生状态，用户ID: {}, 状态: {}", userId, status);
        int rows = userMapper.updateStatus(userId, status);
        if (rows == 0) {
            throw new BusinessException("用户不存在");
        }
    }
    
    /**
     * 删除学生（物理删除）
     */
    @Override
    @Transactional
    public void deleteStudent(Long userId) {
        log.info("删除学生，用户ID: {}", userId);
        int rows = userMapper.deleteById(userId);
        if (rows == 0) {
            throw new BusinessException("用户不存在");
        }
    }
    
    /**
     * 获取所有医生
     */
    @Override
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorMapper.findAll();
        return doctors.stream()
                .map(this::convertToDoctorDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 搜索医生
     */
    @Override
    public List<DoctorDTO> searchDoctors(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllDoctors();
        }
        List<Doctor> doctors = doctorMapper.searchByKeyword(keyword);
        return doctors.stream()
                .map(this::convertToDoctorDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新医生状态
     */
    @Override
    @Transactional
    public void updateDoctorStatus(Long doctorId, Integer status) {
        log.info("更新医生状态，医生ID: {}, 状态: {}", doctorId, status);
        int rows = doctorMapper.updateStatus(doctorId, status);
        if (rows == 0) {
            throw new BusinessException("医生不存在");
        }
    }
    
    /**
     * 删除医生（物理删除）
     */
    @Override
    @Transactional
    public void deleteDoctor(Long doctorId) {
        log.info("删除医生，医生ID: {}", doctorId);
        int rows = doctorMapper.deleteById(doctorId);
        if (rows == 0) {
            throw new BusinessException("医生不存在");
        }
    }
    
    /**
     * 转换为AdminDTO
     */
    private AdminDTO convertToAdminDTO(Admin admin) {
        return AdminDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .role(admin.getRole())
                .status(admin.getStatus())
                .lastLoginAt(admin.getLastLoginAt())
                .build();
    }
    
    /**
     * 转换为UserDTO
     */
    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .studentId(user.getStudentId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .college(user.getCollege())
                .majorClass(user.getMajorClass())
                .gender(user.getGender())
                .status(user.getStatus())
                .checkinStreak(user.getCheckinStreak())
                .counselingHours(user.getCounselingHours())
                .appointmentsCount(user.getAppointmentsCount())
                .build();
    }
    
    /**
     * 转换为DoctorDTO
     */
    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        List<String> tags = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        List<String> certifications = new ArrayList<>();
        
        try {
            if (StringUtils.hasText(doctor.getTags())) {
                tags = objectMapper.readValue(doctor.getTags(), new TypeReference<List<String>>() {});
            }
            if (StringUtils.hasText(doctor.getMethods())) {
                methods = objectMapper.readValue(doctor.getMethods(), new TypeReference<List<String>>() {});
            }
            if (StringUtils.hasText(doctor.getCertifications())) {
                certifications = objectMapper.readValue(doctor.getCertifications(), new TypeReference<List<String>>() {});
            }
        } catch (Exception e) {
            log.warn("解析医生JSON数据失败: {}", e.getMessage());
        }
        
        return DoctorDTO.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .title(doctor.getTitle())
                .phone(doctor.getPhone())
                .email(doctor.getEmail())
                .avatar(doctor.getAvatar())
                .description(doctor.getDescription())
                .years(doctor.getYears())
                .tags(tags)
                .methods(methods)
                .certifications(certifications)
                .rating(doctor.getRating())
                .totalHours(doctor.getTotalHours())
                .helpedCount(doctor.getHelpedCount())
                .positiveRate(doctor.getPositiveRate())
                .available(doctor.getAvailable() != null && doctor.getAvailable() == 1)
                .status(doctor.getStatus())
                .build();
    }
}
