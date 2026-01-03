package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.ChatMessageDTO;
import com.cqust.psychologicalcounseling.dto.ChatSessionDTO;
import com.cqust.psychologicalcounseling.dto.request.SendMessageRequest;
import com.cqust.psychologicalcounseling.dto.response.AiMessageResponse;
import com.cqust.psychologicalcounseling.dto.response.CreateSessionResponse;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface AiChatService {
    
    /**
     * 创建新会话
     *
     * @param userId 用户ID
     * @return 会话信息
     */
    CreateSessionResponse createSession(Long userId);
    
    /**
     * 获取用户的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatSessionDTO> getSessions(Long userId);
    
    /**
     * 获取会话的消息记录
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 消息列表
     */
    List<ChatMessageDTO> getMessages(String sessionId, Long userId);
    
    /**
     * 发送消息并获取AI回复
     *
     * @param request 消息请求
     * @param userId  用户ID
     * @return AI回复
     */
    AiMessageResponse sendMessage(SendMessageRequest request, Long userId);
    
    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     */
    void deleteSession(String sessionId, Long userId);
}
