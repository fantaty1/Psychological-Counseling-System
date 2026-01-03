package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.DoctorDTO;
import com.cqust.psychologicalcounseling.dto.DoctorStatsDTO;
import com.cqust.psychologicalcounseling.dto.DoctorTodayStatsDTO;
import com.cqust.psychologicalcounseling.dto.UpdateDoctorProfileRequest;
import com.cqust.psychologicalcounseling.dto.request.DoctorRegisterRequest;
import com.cqust.psychologicalcounseling.dto.response.DoctorRegisterResponse;
import com.cqust.psychologicalcounseling.entity.Doctor;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.AppointmentMapper;
import com.cqust.psychologicalcounseling.mapper.DoctorChatSessionMapper;
import com.cqust.psychologicalcounseling.mapper.DoctorMapper;
import com.cqust.psychologicalcounseling.service.DoctorService;
import com.cqust.psychologicalcounseling.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 咨询师服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    
    private final DoctorMapper doctorMapper;
    private final AppointmentMapper appointmentMapper;
    private final DoctorChatSessionMapper chatSessionMapper;
    
    /**
     * 咨询师注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @Override
    @Transactional
    public DoctorRegisterResponse register(DoctorRegisterRequest request) {
        log.info("咨询师注册，手机号: {}", request.getPhone());
        
        // 检查手机号是否已存在
        if (doctorMapper.findByPhone(request.getPhone()).isPresent()) {
            throw new BusinessException("该手机号已被注册");
        }
        
        // 检查邮箱是否已存在
        if (doctorMapper.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 将列表转换为JSON字符串
        String tagsJson = listToJson(request.getTags());
        String methodsJson = listToJson(request.getMethods());
        String certificationsJson = listToJson(request.getCertifications());
        
        // 创建咨询师实体
        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .title(request.getTitle())
                .phone(request.getPhone())
                .password(PasswordUtil.encode(request.getPassword()))
                .email(request.getEmail())
                .description(request.getDescription())
                .years(request.getYears())
                .tags(tagsJson)
                .methods(methodsJson)
                .certifications(certificationsJson)
                .rating(BigDecimal.valueOf(5.0)) // 初始评分5.0
                .totalHours(BigDecimal.ZERO)
                .helpedCount(0)
                .positiveRate(100) // 初始好评率100%
                .available(1) // 默认可预约
                .status(1) // 正常状态
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 保存咨询师
        doctorMapper.insert(doctor);
        
        log.info("咨询师注册成功，咨询师ID: {}", doctor.getId());
        
        return DoctorRegisterResponse.builder()
                .doctorId(doctor.getId())
                .name(doctor.getName())
                .build();
    }
    
    /**
     * 根据ID获取咨询师
     *
     * @param doctorId 咨询师ID
     * @return 咨询师实体
     */
    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorMapper.findById(doctorId)
                .orElseThrow(() -> new BusinessException("咨询师不存在"));
    }
    
    /**
     * 根据手机号获取咨询师
     *
     * @param phone 手机号
     * @return 咨询师实体
     */
    @Override
    public Doctor getDoctorByPhone(String phone) {
        return doctorMapper.findByPhone(phone)
                .orElseThrow(() -> new BusinessException("咨询师不存在"));
    }
    
    /**
     * 将列表转换为JSON字符串
     * 手动构建JSON数组，避免依赖ObjectMapper
     *
     * @param list 列表
     * @return JSON字符串
     */
    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return "[" + list.stream()
                .map(s -> "\"" + s.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(",")) + "]";
    }
    
    /**
     * 将JSON字符串转换为列表
     *
     * @param json JSON字符串
     * @return 列表
     */
    private List<String> jsonToList(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return Collections.emptyList();
        }
        // 简单的JSON数组解析
        String content = json.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
        }
        if (content.isEmpty()) {
            return Collections.emptyList();
        }
        // 分割并去除引号
        return Arrays.stream(content.split(","))
                .map(String::trim)
                .map(s -> {
                    if (s.startsWith("\"") && s.endsWith("\"")) {
                        return s.substring(1, s.length() - 1);
                    }
                    return s;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public DoctorDTO getDoctorProfile(Long doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        return convertToDTO(doctor);
    }
    
    @Override
    @Transactional
    public DoctorDTO updateDoctorProfile(Long doctorId, UpdateDoctorProfileRequest request) {
        Doctor doctor = getDoctorById(doctorId);
        
        // 更新字段
        if (request.getName() != null) {
            doctor.setName(request.getName());
        }
        if (request.getTitle() != null) {
            doctor.setTitle(request.getTitle());
        }
        if (request.getPhone() != null) {
            doctor.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            doctor.setEmail(request.getEmail());
        }
        if (request.getDescription() != null) {
            doctor.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            doctor.setLocation(request.getLocation());
        }
        if (request.getYears() != null) {
            doctor.setYears(request.getYears());
        }
        if (request.getTags() != null) {
            doctor.setTags(listToJson(request.getTags()));
        }
        if (request.getMethods() != null) {
            doctor.setMethods(listToJson(request.getMethods()));
        }
        
        doctor.setUpdatedAt(LocalDateTime.now());
        doctorMapper.update(doctor);
        
        return convertToDTO(doctor);
    }
    
    @Override
    public DoctorTodayStatsDTO getTodayStats(Long doctorId) {
        LocalDate today = LocalDate.now();
        
        int todayAppointments = appointmentMapper.countTodayByDoctorId(doctorId, today);
        int completed = appointmentMapper.countTodayCompletedByDoctorId(doctorId, today);
        int pending = appointmentMapper.countAllPendingByDoctorId(doctorId); // 改为统计所有待处理
        int unreadMessages = chatSessionMapper.countDoctorUnread(doctorId);
        
        return DoctorTodayStatsDTO.builder()
                .todayAppointments(todayAppointments)
                .completed(completed)
                .pending(pending)
                .unreadMessages(unreadMessages)
                .build();
    }
    
    @Override
    public List<DoctorDTO> getAllAvailableDoctors() {
        List<Doctor> doctors = doctorMapper.findAllAvailable();
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DoctorDTO> searchDoctors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAvailableDoctors();
        }
        List<Doctor> doctors = doctorMapper.searchByKeyword(keyword.trim());
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将Doctor实体转换为DoctorDTO
     */
    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorStatsDTO stats = DoctorStatsDTO.builder()
                .totalHours(doctor.getTotalHours() != null ? doctor.getTotalHours() : BigDecimal.ZERO)
                .helpedCount(doctor.getHelpedCount() != null ? doctor.getHelpedCount() : 0)
                .positiveRate(doctor.getPositiveRate() != null ? doctor.getPositiveRate() : 100)
                .build();
        
        return DoctorDTO.builder()
            .id(doctor.getId())
            .name(doctor.getName())
            .avatar(doctor.getAvatar())
            .title(doctor.getTitle())
            .phone(doctor.getPhone())
            .email(doctor.getEmail())
            .description(doctor.getDescription())
            .years(doctor.getYears())
            .location(doctor.getLocation())
            .rating(doctor.getRating() != null ? doctor.getRating() : BigDecimal.valueOf(5.0))
            .tags(jsonToList(doctor.getTags()))
            .methods(jsonToList(doctor.getMethods()))
            .certifications(jsonToList(doctor.getCertifications()))
            .stats(stats)
            .available(doctor.getAvailable() == 1)
            .build();
    }
}
