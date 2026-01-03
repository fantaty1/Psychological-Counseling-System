package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.DoctorTodayStatsDTO;
import com.cqust.psychologicalcounseling.dto.UpdateDoctorProfileRequest;
import com.cqust.psychologicalcounseling.dto.request.DoctorRegisterRequest;
import com.cqust.psychologicalcounseling.dto.response.DoctorRegisterResponse;
import com.cqust.psychologicalcounseling.entity.Doctor;

import java.util.List;

/**
 * 咨询师服务接口
 * 定义咨询师相关的业务操作
 */
public interface DoctorService {
    
    /**
     * 咨询师注册
     *
     * @param request 注册请求
     * @return 注册响应（包含咨询师ID）
     */
    DoctorRegisterResponse register(DoctorRegisterRequest request);
    
    /**
     * 根据ID获取咨询师
     *
     * @param doctorId 咨询师ID
     * @return 咨询师实体
     */
    Doctor getDoctorById(Long doctorId);
    
    /**
     * 根据手机号获取咨询师
     *
     * @param phone 手机号
     * @return 咨询师实体
     */
    Doctor getDoctorByPhone(String phone);
    
    /**
     * 获取咨询师个人信息DTO
     *
     * @param doctorId 咨询师ID
     * @return 咨询师DTO
     */
    DoctorDTO getDoctorProfile(Long doctorId);
    
    /**
     * 更新咨询师个人信息
     *
     * @param doctorId 咨询师ID
     * @param request 更新请求
     * @return 更新后的咨询师DTO
     */
    DoctorDTO updateDoctorProfile(Long doctorId, UpdateDoctorProfileRequest request);
    
    /**
     * 获取咨询师今日统计
     *
     * @param doctorId 咨询师ID
     * @return 今日统计DTO
     */
    DoctorTodayStatsDTO getTodayStats(Long doctorId);
    
    /**
     * 获取所有可用咨询师列表
     *
     * @return 咨询师列表
     */
    List<DoctorDTO> getAllAvailableDoctors();
    
    /**
     * 搜索咨询师
     *
     * @param keyword 关键词
     * @return 咨询师列表
     */
    List<DoctorDTO> searchDoctors(String keyword);
}
