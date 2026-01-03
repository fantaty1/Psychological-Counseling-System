package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.UserDTO;
import com.cqust.psychologicalcounseling.dto.request.LoginRequest;
import com.cqust.psychologicalcounseling.dto.request.RegisterRequest;
import com.cqust.psychologicalcounseling.dto.request.UpdateProfileRequest;
import com.cqust.psychologicalcounseling.dto.response.LoginResponse;
import com.cqust.psychologicalcounseling.dto.response.RegisterResponse;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应（包含用户ID）
     */
    RegisterResponse register(RegisterRequest request);
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 获取当前用户个人信息
     *
     * @param userId 用户ID
     * @return 用户信息DTO
     */
    UserDTO getProfile(Long userId);
    
    /**
     * 更新用户个人信息
     *
     * @param userId  用户ID
     * @param request 更新请求
     */
    void updateProfile(Long userId, UpdateProfileRequest request);
    
    /**
     * 更新用户签到连续天数
     *
     * @param userId 用户ID
     * @param streak 新的连续天数
     */
    void updateCheckinStreak(Long userId, Integer streak);
    
    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息DTO
     */
    UserDTO getUserById(Long userId);
    
    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
