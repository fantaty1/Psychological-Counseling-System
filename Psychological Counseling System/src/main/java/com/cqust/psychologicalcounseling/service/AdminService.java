package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.AdminDTO;
import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.UserDTO;
import com.cqust.psychologicalcounseling.dto.request.AdminLoginRequest;
import com.cqust.psychologicalcounseling.dto.response.AdminLoginResponse;

import java.util.List;

/**
 * 管理员服务接口
 * 定义管理员相关的业务操作
 */
public interface AdminService {
    
    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request);
    
    /**
     * 获取管理员信息
     *
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    AdminDTO getAdminInfo(Long adminId);
    
    /**
     * 获取所有学生用户
     *
     * @return 学生用户列表
     */
    List<UserDTO> getAllStudents();
    
    /**
     * 搜索学生用户
     *
     * @param keyword 关键词
     * @return 学生用户列表
     */
    List<UserDTO> searchStudents(String keyword);
    
    /**
     * 更新学生状态
     *
     * @param userId 用户ID
     * @param status 状态（0-禁用 1-正常）
     */
    void updateStudentStatus(Long userId, Integer status);
    
    /**
     * 删除学生
     *
     * @param userId 用户ID
     */
    void deleteStudent(Long userId);
    
    /**
     * 获取所有医生
     *
     * @return 医生列表
     */
    List<DoctorDTO> getAllDoctors();
    
    /**
     * 搜索医生
     *
     * @param keyword 关键词
     * @return 医生列表
     */
    List<DoctorDTO> searchDoctors(String keyword);
    
    /**
     * 更新医生状态
     *
     * @param doctorId 医生ID
     * @param status 状态（0-待审核 1-正常 2-下架）
     */
    void updateDoctorStatus(Long doctorId, Integer status);
    
    /**
     * 删除医生
     *
     * @param doctorId 医生ID
     */
    void deleteDoctor(Long doctorId);
}
