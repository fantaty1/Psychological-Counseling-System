package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.*;

import java.util.List;

/**
 * 医生-学生聊天服务接口
 */
public interface DoctorChatService {
    
    /**
     * 获取咨询师的聊天会话列表
     * @param doctorId 咨询师ID
     * @return 会话列表
     */
    List<DoctorChatSessionDTO> getSessions(Long doctorId);
    
    /**
     * 获取会话消息记录
     * @param sessionId 会话ID
     * @param doctorId 咨询师ID（用于验证权限）
     * @return 消息列表（含学生信息）
     */
    SessionMessagesDTO getSessionMessages(String sessionId, Long doctorId);
    
    /**
     * 咨询师发送消息
     * @param request 发送消息请求
     * @param doctorId 咨询师ID
     * @return 发送的消息
     */
    DoctorChatMessageDTO sendMessage(DoctorSendMessageRequest request, Long doctorId);
    
    /**
     * 获取咨询师待回复的消息列表
     * @param doctorId 咨询师ID
     * @return 待回复消息列表
     */
    List<DoctorChatMessageDTO> getPendingMessages(Long doctorId);
    
    /**
     * 获取或创建会话
     * @param doctorId 咨询师ID
     * @param studentId 学生ID
     * @return 会话ID
     */
    String getOrCreateSession(Long doctorId, Long studentId);
    
    /**
     * 学生发送消息给咨询师
     * @param sessionId 会话ID
     * @param studentId 学生ID
     * @param content 消息内容
     * @return 发送的消息
     */
    DoctorChatMessageDTO studentSendMessage(String sessionId, Long studentId, String content);
    
    /**
     * 统计咨询师未读消息数
     * @param doctorId 咨询师ID
     * @return 未读消息数
     */
    int countUnreadMessages(Long doctorId);
    
    // ==================== 学生端方法 ====================
    
    /**
     * 获取学生的聊天会话列表
     * @param studentId 学生ID
     * @return 会话列表
     */
    List<DoctorChatSessionDTO> getStudentSessions(Long studentId);
    
    /**
     * 学生获取会话消息记录
     * @param sessionId 会话ID
     * @param studentId 学生ID（用于验证权限）
     * @return 消息列表（含医生信息）
     */
    SessionMessagesDTO getStudentSessionMessages(String sessionId, Long studentId);
    
    /**
     * 统计学生未读消息数
     * @param studentId 学生ID
     * @return 未读消息数
     */
    int countStudentUnreadMessages(Long studentId);
}
