package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.*;
import com.cqust.psychologicalcounseling.entity.DoctorChatMessage;
import com.cqust.psychologicalcounseling.entity.DoctorChatSession;
import com.cqust.psychologicalcounseling.entity.User;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.DoctorChatMessageMapper;
import com.cqust.psychologicalcounseling.mapper.DoctorChatSessionMapper;
import com.cqust.psychologicalcounseling.mapper.DoctorMapper;
import com.cqust.psychologicalcounseling.mapper.UserMapper;
import com.cqust.psychologicalcounseling.service.DoctorChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 医生-学生聊天服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorChatServiceImpl implements DoctorChatService {
    
    private final DoctorChatSessionMapper sessionMapper;
    private final DoctorChatMessageMapper messageMapper;
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<DoctorChatSessionDTO> getSessions(Long doctorId) {
        List<DoctorChatSession> sessions = sessionMapper.findByDoctorId(doctorId);
        
        return sessions.stream().map(session -> {
            DoctorChatSessionDTO dto = DoctorChatSessionDTO.builder()
                    .sessionId(session.getSessionId())
                    .studentId(session.getStudentId())
                    .lastMessage(session.getLastMessage())
                    .unreadCount(session.getDoctorUnreadCount())
                    .updatedAt(session.getUpdatedAt() != null ? session.getUpdatedAt().format(DATETIME_FORMATTER) : null)
                    .build();
            
            // 填充学生信息
            userMapper.findById(session.getStudentId()).ifPresent(user -> {
                dto.setStudentName(user.getUsername());
                dto.setStudentAvatar(user.getAvatar());
                dto.setCollege(user.getCollege());
            });
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SessionMessagesDTO getSessionMessages(String sessionId, Long doctorId) {
        // 验证会话归属
        DoctorChatSession session = sessionMapper.findBySessionId(sessionId);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!session.getDoctorId().equals(doctorId)) {
            throw new BusinessException(403, "无权访问此会话");
        }
        
        // 标记学生消息为已读
        messageMapper.markAsRead(sessionId, "student");
        sessionMapper.clearDoctorUnread(sessionId);
        
        // 获取消息列表
        List<DoctorChatMessage> messages = messageMapper.findBySessionId(sessionId);
        List<DoctorChatMessageDTO> messageDTOs = messages.stream()
                .map(this::convertMessageToDTO)
                .collect(Collectors.toList());
        
        // 构建返回数据
        SessionMessagesDTO dto = SessionMessagesDTO.builder()
                .studentId(session.getStudentId())
                .messages(messageDTOs)
                .build();
        
        // 填充学生信息
        userMapper.findById(session.getStudentId()).ifPresent(user -> {
            dto.setStudentName(user.getUsername());
            dto.setStudentAvatar(user.getAvatar());
            dto.setCollege(user.getCollege());
        });
        
        return dto;
    }
    
    @Override
    @Transactional
    public DoctorChatMessageDTO sendMessage(DoctorSendMessageRequest request, Long doctorId) {
        // 验证会话归属
        DoctorChatSession session = sessionMapper.findBySessionId(request.getSessionId());
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!session.getDoctorId().equals(doctorId)) {
            throw new BusinessException(403, "无权访问此会话");
        }
        
        // 创建消息
        DoctorChatMessage message = DoctorChatMessage.builder()
                .sessionId(request.getSessionId())
                .role("doctor")
                .senderId(doctorId)
                .content(request.getContent())
                .isRead(0)
                .build();
        
        messageMapper.insert(message);
        
        // 更新会话最后消息和未读数
        String lastMessage = request.getContent();
        if (lastMessage.length() > 50) {
            lastMessage = lastMessage.substring(0, 50) + "...";
        }
        sessionMapper.updateLastMessage(request.getSessionId(), lastMessage, 0, 1);
        
        return convertMessageToDTO(messageMapper.findById(message.getId()));
    }
    
    @Override
    public List<DoctorChatMessageDTO> getPendingMessages(Long doctorId) {
        List<DoctorChatMessage> messages = messageMapper.findPendingMessagesByDoctorId(doctorId);
        return messages.stream()
                .map(this::convertMessageToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public String getOrCreateSession(Long doctorId, Long studentId) {
        // 查找现有会话
        DoctorChatSession session = sessionMapper.findByDoctorIdAndStudentId(doctorId, studentId);
        if (session != null) {
            return session.getSessionId();
        }
        
        // 创建新会话
        String sessionId = UUID.randomUUID().toString();
        DoctorChatSession newSession = DoctorChatSession.builder()
                .sessionId(sessionId)
                .doctorId(doctorId)
                .studentId(studentId)
                .lastMessage("")
                .doctorUnreadCount(0)
                .studentUnreadCount(0)
                .build();
        
        sessionMapper.insert(newSession);
        
        return sessionId;
    }
    
    @Override
    @Transactional
    public DoctorChatMessageDTO studentSendMessage(String sessionId, Long studentId, String content) {
        // 验证会话归属
        DoctorChatSession session = sessionMapper.findBySessionId(sessionId);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!session.getStudentId().equals(studentId)) {
            throw new BusinessException(403, "无权访问此会话");
        }
        
        // 创建消息
        DoctorChatMessage message = DoctorChatMessage.builder()
                .sessionId(sessionId)
                .role("student")
                .senderId(studentId)
                .content(content)
                .isRead(0)
                .build();
        
        messageMapper.insert(message);
        
        // 更新会话最后消息和未读数
        String lastMessage = content;
        if (lastMessage.length() > 50) {
            lastMessage = lastMessage.substring(0, 50) + "...";
        }
        sessionMapper.updateLastMessage(sessionId, lastMessage, 1, 0);
        
        return convertMessageToDTO(messageMapper.findById(message.getId()));
    }
    
    @Override
    public int countUnreadMessages(Long doctorId) {
        return sessionMapper.countDoctorUnread(doctorId);
    }
    
    // ==================== 学生端方法 ====================
    
    @Override
    public List<DoctorChatSessionDTO> getStudentSessions(Long studentId) {
        List<DoctorChatSession> sessions = sessionMapper.findByStudentId(studentId);
        
        return sessions.stream().map(session -> {
            DoctorChatSessionDTO dto = DoctorChatSessionDTO.builder()
                    .sessionId(session.getSessionId())
                    .doctorId(session.getDoctorId())
                    .lastMessage(session.getLastMessage())
                    .unreadCount(session.getStudentUnreadCount())
                    .updatedAt(session.getUpdatedAt() != null ? session.getUpdatedAt().format(DATETIME_FORMATTER) : null)
                    .build();
            
            // 填充医生信息
            doctorMapper.findById(session.getDoctorId()).ifPresent(doctor -> {
                dto.setDoctorName(doctor.getName());
                dto.setDoctorAvatar(doctor.getAvatar());
                dto.setDoctorTitle(doctor.getTitle());
            });
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SessionMessagesDTO getStudentSessionMessages(String sessionId, Long studentId) {
        // 验证会话归属
        DoctorChatSession session = sessionMapper.findBySessionId(sessionId);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!session.getStudentId().equals(studentId)) {
            throw new BusinessException(403, "无权访问此会话");
        }
        
        // 标记医生消息为已读
        messageMapper.markAsRead(sessionId, "doctor");
        sessionMapper.clearStudentUnread(sessionId);
        
        // 获取消息列表
        List<DoctorChatMessage> messages = messageMapper.findBySessionId(sessionId);
        List<DoctorChatMessageDTO> messageDTOs = messages.stream()
                .map(this::convertMessageToDTO)
                .collect(Collectors.toList());
        
        // 构建返回数据
        SessionMessagesDTO dto = SessionMessagesDTO.builder()
                .doctorId(session.getDoctorId())
                .messages(messageDTOs)
                .build();
        
        // 填充医生信息
        doctorMapper.findById(session.getDoctorId()).ifPresent(doctor -> {
            dto.setDoctorName(doctor.getName());
            dto.setDoctorAvatar(doctor.getAvatar());
            dto.setDoctorTitle(doctor.getTitle());
        });
        
        return dto;
    }
    
    @Override
    public int countStudentUnreadMessages(Long studentId) {
        return sessionMapper.countStudentUnread(studentId);
    }
    
    /**
     * 转换消息为DTO
     */
    private DoctorChatMessageDTO convertMessageToDTO(DoctorChatMessage message) {
        return DoctorChatMessageDTO.builder()
                .id(message.getId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt() != null ? message.getCreatedAt().format(DATETIME_FORMATTER) : null)
                .build();
    }
}
