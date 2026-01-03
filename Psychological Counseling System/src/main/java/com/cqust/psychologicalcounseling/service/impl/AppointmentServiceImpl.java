package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.*;
import com.cqust.psychologicalcounseling.entity.Appointment;
import com.cqust.psychologicalcounseling.entity.Doctor;
import com.cqust.psychologicalcounseling.entity.User;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.AppointmentMapper;
import com.cqust.psychologicalcounseling.mapper.DoctorMapper;
import com.cqust.psychologicalcounseling.mapper.UserMapper;
import com.cqust.psychologicalcounseling.service.AppointmentService;
import com.cqust.psychologicalcounseling.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    
    private final AppointmentMapper appointmentMapper;
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    private final DoctorScheduleService doctorScheduleService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public List<AppointmentDTO> getTodayAppointments(Long doctorId) {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentMapper.findTodayByDoctorId(doctorId, today);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AppointmentListDTO getAppointments(Long doctorId, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Appointment> appointments = appointmentMapper.findByDoctorIdWithPage(doctorId, status, offset, pageSize);
        int total = appointmentMapper.countByDoctorId(doctorId, status);
        
        List<AppointmentDTO> dtoList = appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return AppointmentListDTO.builder()
                .list(dtoList)
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .build();
    }
    
    @Override
    @Transactional
    public AppointmentDTO confirmAppointment(Long appointmentId, Long doctorId, String location) {
        Appointment appointment = validateAppointmentOwnership(appointmentId, doctorId);
        
        if (!"pending".equals(appointment.getStatus())) {
            throw new BusinessException(400, "只能确认待处理的预约");
        }
        
        appointmentMapper.confirm(appointmentId, location);
        
        // 标记时间段为已预约
        doctorScheduleService.markSlotAsBooked(doctorId, 
                appointment.getAppointmentDate().format(DATE_FORMATTER), 
                appointment.getTimeSlot());
        
        return getAppointmentById(appointmentId);
    }
    
    @Override
    @Transactional
    public AppointmentDTO rejectAppointment(Long appointmentId, Long doctorId, String reason) {
        Appointment appointment = validateAppointmentOwnership(appointmentId, doctorId);
        
        if (!"pending".equals(appointment.getStatus())) {
            throw new BusinessException(400, "只能拒绝待处理的预约");
        }
        
        appointmentMapper.reject(appointmentId, reason);
        
        return getAppointmentById(appointmentId);
    }
    
    @Override
    @Transactional
    public AppointmentDTO completeAppointment(Long appointmentId, Long doctorId, String notes) {
        Appointment appointment = validateAppointmentOwnership(appointmentId, doctorId);
        
        if (!"confirmed".equals(appointment.getStatus())) {
            throw new BusinessException(400, "只能完成已确认的预约");
        }
        
        appointmentMapper.complete(appointmentId, notes);
        
        // 更新咨询师统计数据
        doctorMapper.findById(doctorId).ifPresent(doctor -> {
            int newHelpedCount = (doctor.getHelpedCount() != null ? doctor.getHelpedCount() : 0) + 1;
            double newTotalHours = (doctor.getTotalHours() != null ? doctor.getTotalHours().doubleValue() : 0.0) + 1.0;
            doctorMapper.updateStats(doctorId, doctor.getRating(), 
                    java.math.BigDecimal.valueOf(newTotalHours), newHelpedCount, doctor.getPositiveRate());
        });
        
        return getAppointmentById(appointmentId);
    }
    
    @Override
    @Transactional
    public AppointmentDTO createAppointment(CreateAppointmentRequest request, Long studentId) {
        // 验证咨询师存在
        Doctor doctor = doctorMapper.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessException(404, "咨询师不存在"));
        
        // 验证时间段可用
        LocalDate appointmentDate = LocalDate.parse(request.getDate(), DATE_FORMATTER);
        if (!doctorScheduleService.isSlotAvailable(request.getDoctorId(), request.getDate(), request.getTimeSlot())) {
            throw new BusinessException(400, "该时间段不可预约");
        }
        
        // 检查是否已有相同时间的预约
        int existingCount = appointmentMapper.checkTimeSlotBooked(request.getDoctorId(), appointmentDate, request.getTimeSlot());
        if (existingCount > 0) {
            throw new BusinessException(400, "该时间段已被预约");
        }
        
        // 创建预约
        Appointment appointment = Appointment.builder()
                .studentId(studentId)
                .doctorId(request.getDoctorId())
                .appointmentDate(appointmentDate)
                .timeSlot(request.getTimeSlot())
                .type(request.getType())
                .status("pending")
                .description(request.getDescription())
                .build();
        
        appointmentMapper.insert(appointment);
        
        return getAppointmentById(appointment.getId());
    }
    
    @Override
    public AppointmentDTO getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentMapper.findById(appointmentId);
        if (appointment == null) {
            throw new BusinessException(404, "预约不存在");
        }
        return convertToDTO(appointment);
    }
    
    @Override
    public List<AppointmentDTO> getStudentAppointments(Long studentId) {
        List<Appointment> appointments = appointmentMapper.findByStudentId(studentId);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId, Long studentId) {
        Appointment appointment = appointmentMapper.findById(appointmentId);
        if (appointment == null) {
            throw new BusinessException(404, "预约不存在");
        }
        
        if (!appointment.getStudentId().equals(studentId)) {
            throw new BusinessException(403, "无权操作此预约");
        }
        
        if ("cancelled".equals(appointment.getStatus()) || "completed".equals(appointment.getStatus())) {
            throw new BusinessException(400, "该预约无法取消");
        }
        
        // 更新预约状态
        appointmentMapper.updateStatus(appointmentId, "cancelled");
        
        // 释放排班时间段
        String date = appointment.getAppointmentDate().format(DATE_FORMATTER);
        doctorScheduleService.markSlotAsAvailable(appointment.getDoctorId(), date, appointment.getTimeSlot());
        
        return getAppointmentById(appointmentId);
    }
    
    /**
     * 验证预约归属权
     */
    private Appointment validateAppointmentOwnership(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentMapper.findById(appointmentId);
        if (appointment == null) {
            throw new BusinessException(404, "预约不存在");
        }
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new BusinessException(403, "无权操作此预约");
        }
        return appointment;
    }
    
    /**
     * 转换为DTO
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = AppointmentDTO.builder()
                .id(appointment.getId())
                .studentId(appointment.getStudentId())
                .doctorId(appointment.getDoctorId())
                .date(appointment.getAppointmentDate().format(DATE_FORMATTER))
                .timeSlot(appointment.getTimeSlot())
                .type(appointment.getType())
                .status(appointment.getStatus())
                .description(appointment.getDescription())
                .location(appointment.getLocation())
                .createdAt(appointment.getCreatedAt() != null ? appointment.getCreatedAt().toString() : null)
                .build();
        
        // 填充学生信息
        userMapper.findById(appointment.getStudentId()).ifPresent(user -> {
            // 优先使用username，如果为空则使用studentId
            String displayName = (user.getUsername() != null && !user.getUsername().isEmpty()) 
                ? user.getUsername() 
                : user.getStudentId();
            dto.setStudentName(displayName);
            dto.setStudentIdNo(user.getStudentId());
            dto.setStudentAvatar(user.getAvatar());
            dto.setStudentCollege(user.getCollege());
        });
        
        // 填充咨询师信息
        doctorMapper.findById(appointment.getDoctorId()).ifPresent(doctor -> {
            dto.setDoctorName(doctor.getName());
            dto.setDoctorAvatar(doctor.getAvatar());
        });
        
        return dto;
    }
}
