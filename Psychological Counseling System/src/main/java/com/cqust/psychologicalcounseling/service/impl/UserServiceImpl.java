package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.UserDTO;
import com.cqust.psychologicalcounseling.dto.request.LoginRequest;
import com.cqust.psychologicalcounseling.dto.request.RegisterRequest;
import com.cqust.psychologicalcounseling.dto.request.UpdateProfileRequest;
import com.cqust.psychologicalcounseling.dto.response.LoginResponse;
import com.cqust.psychologicalcounseling.dto.response.RegisterResponse;
import com.cqust.psychologicalcounseling.entity.Doctor;
import com.cqust.psychologicalcounseling.entity.User;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.DoctorMapper;
import com.cqust.psychologicalcounseling.mapper.UserMapper;
import com.cqust.psychologicalcounseling.service.DoctorService;
import com.cqust.psychologicalcounseling.service.UserService;
import com.cqust.psychologicalcounseling.util.JwtUtil;
import com.cqust.psychologicalcounseling.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("用户注册，学号: {}", request.getStudentId());
        
        // 检查学号是否已存在
        if (userMapper.countByStudentId(request.getStudentId()) > 0) {
            throw new BusinessException("该学号已被注册");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.countByEmail(request.getEmail()) > 0) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 创建用户实体
        User user = User.builder()
                .username(request.getUsername())
                .studentId(request.getStudentId())
                .password(PasswordUtil.encode(request.getPassword()))
                .email(request.getEmail())
                .college(request.getCollege())
                .status(1) // 正常状态
                .checkinStreak(0)
                .counselingHours(BigDecimal.ZERO)
                .appointmentsCount(0)
                .allowPeerView(1)
                .allowNotification(1)
                .build();
        
        // 保存用户
        userMapper.insert(user);
        
        log.info("用户注册成功，用户ID: {}", user.getId());
        
        return RegisterResponse.builder()
                .userId(user.getId())
                .build();
    }
    
    /**
     * 用户登录（支持学生和咨询师）
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        String role = request.getRole();
        log.info("用户登录，用户名: {}, 角色: {}", request.getUsername(), role);
        
        // 根据角色类型分别处理登录
        if ("doctor".equals(role)) {
            return loginAsDoctor(request);
        } else {
            return loginAsStudent(request);
        }
    }
    
    /**
     * 学生登录
     */
    private LoginResponse loginAsStudent(LoginRequest request) {
        // 根据学号或邮箱查询用户
        User user = userMapper.findByStudentIdOrEmail(request.getUsername())
                .orElseThrow(() -> new BusinessException("学号/邮箱或密码错误"));
        
        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("学号/邮箱或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用");
        }
        
        // 生成JWT token（role前缀用于区分）
        String token = jwtUtil.generateToken(user.getId(), "student:" + user.getUsername());
        
        log.info("学生登录成功，用户ID: {}", user.getId());
        
        return LoginResponse.builder()
                .token(token)
                .user(UserDTO.fromEntity(user))
                .role("student")
                .build();
    }
    
    /**
     * 咨询师登录
     */
    private LoginResponse loginAsDoctor(LoginRequest request) {
        // 根据手机号或邮箱查询咨询师
        Doctor doctor = doctorMapper.findByPhoneOrEmail(request.getUsername())
                .orElseThrow(() -> new BusinessException("手机号/邮箱或密码错误"));
        
        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), doctor.getPassword())) {
            throw new BusinessException("手机号/邮箱或密码错误");
        }
        
        // 检查咨询师状态
        if (doctor.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用");
        }
        
        // 生成JWT token（role前缀用于区分）
        String token = jwtUtil.generateToken(doctor.getId(), "doctor:" + doctor.getName());
        
        log.info("咨询师登录成功，咨询师ID: {}", doctor.getId());
        
        // 构建简化的医生信息返回
        DoctorLoginDTO doctorInfo = DoctorLoginDTO.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .avatar(doctor.getAvatar())
                .title(doctor.getTitle())
                .phone(doctor.getPhone())
                .email(doctor.getEmail())
                .build();
        
        return LoginResponse.builder()
                .token(token)
                .user(doctorInfo)
                .role("doctor")
                .build();
    }
    
    /**
     * 咨询师登录返回的简化信息
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DoctorLoginDTO {
        private Long id;
        private String name;
        private String avatar;
        private String title;
        private String phone;
        private String email;
    }
    
    /**
     * 获取当前用户个人信息
     *
     * @param userId 用户ID
     * @return 用户信息DTO
     */
    @Override
    public UserDTO getProfile(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return UserDTO.fromEntity(user);
    }
    
    /**
     * 更新用户个人信息
     *
     * @param userId  用户ID
     * @param request 更新请求
     */
    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("更新用户信息，用户ID: {}", userId);
        
        // 检查用户是否存在
        User existingUser = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 如果更新邮箱，检查邮箱是否被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userMapper.countByEmail(request.getEmail()) > 0) {
                throw new BusinessException("该邮箱已被其他用户使用");
            }
        }
        
        // 构建更新实体
        User updateUser = User.builder()
                .id(userId)
                .username(request.getName())
                .college(request.getCollege())
                .majorClass(request.getMajorClass())
                .phone(request.getPhone())
                .email(request.getEmail())
                .avatar(request.getAvatar())
                .build();
        
        userMapper.update(updateUser);
        
        log.info("用户信息更新成功，用户ID: {}", userId);
    }
    
    /**
     * 更新用户签到连续天数
     *
     * @param userId 用户ID
     * @param streak 新的连续天数
     */
    @Override
    @Transactional
    public void updateCheckinStreak(Long userId, Integer streak) {
        userMapper.updateCheckinStreak(userId, streak);
    }
    
    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息DTO
     */
    @Override
    public UserDTO getUserById(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return UserDTO.fromEntity(user);
    }
    
    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }
        
        // 验证新密码
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码长度至少6位");
        }
        
        // 加密新密码并更新
        String encryptedPassword = PasswordUtil.encode(newPassword);
        userMapper.updatePassword(userId, encryptedPassword);
        
        log.info("用户密码修改成功，用户ID: {}", userId);
    }
}
