package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.ApiResponse;
import com.cqust.psychologicalcounseling.dto.request.DoctorRegisterRequest;
import com.cqust.psychologicalcounseling.dto.request.LoginRequest;
import com.cqust.psychologicalcounseling.dto.request.RegisterRequest;
import com.cqust.psychologicalcounseling.dto.response.DoctorRegisterResponse;
import com.cqust.psychologicalcounseling.dto.response.LoginResponse;
import com.cqust.psychologicalcounseling.dto.response.RegisterResponse;
import com.cqust.psychologicalcounseling.service.DoctorService;
import com.cqust.psychologicalcounseling.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册、登录等认证相关请求
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final DoctorService doctorService;
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到注册请求，学号: {}", request.getStudentId());
        RegisterResponse response = userService.register(request);
        return ApiResponse.success(response, "注册成功");
    }
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含token和用户信息）
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求，用户名: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ApiResponse.success(response, "登录成功");
    }
    
    /**
     * 咨询师注册
     *
     * @param request 咨询师注册请求
     * @return 注册响应
     */
    @PostMapping("/doctor/register")
    public ApiResponse<DoctorRegisterResponse> doctorRegister(@Valid @RequestBody DoctorRegisterRequest request) {
        log.info("收到咨询师注册请求，手机号: {}", request.getPhone());
        DoctorRegisterResponse response = doctorService.register(request);
        return ApiResponse.success(response, "注册成功");
    }
}
