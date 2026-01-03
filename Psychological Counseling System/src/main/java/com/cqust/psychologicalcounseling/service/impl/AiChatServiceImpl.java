package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.ChatMessageDTO;
import com.cqust.psychologicalcounseling.dto.ChatSessionDTO;
import com.cqust.psychologicalcounseling.dto.request.SendMessageRequest;
import com.cqust.psychologicalcounseling.dto.response.AiMessageResponse;
import com.cqust.psychologicalcounseling.dto.response.CreateSessionResponse;
import com.cqust.psychologicalcounseling.entity.ChatMessage;
import com.cqust.psychologicalcounseling.entity.ChatSession;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.ChatMessageMapper;
import com.cqust.psychologicalcounseling.mapper.ChatSessionMapper;
import com.cqust.psychologicalcounseling.service.AiChatService;
import com.cqust.psychologicalcounseling.util.AiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI聊天服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final AiUtil aiUtil;
    
    /**
     * 创建新会话
     *
     * @param userId 用户ID
     * @return 会话信息
     */
    @Override
    @Transactional
    public CreateSessionResponse createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        
        ChatSession session = ChatSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .title("新会话")
                .lastMessage("")
                .status(1)
                .build();
        
        chatSessionMapper.insert(session);
        
        log.info("创建新会话成功，sessionId: {}, userId: {}", sessionId, userId);
        
        return CreateSessionResponse.builder()
                .sessionId(sessionId)
                .title("新会话")
                .build();
    }
    
    /**
     * 获取用户的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @Override
    public List<ChatSessionDTO> getSessions(Long userId) {
        List<ChatSession> sessions = chatSessionMapper.findByUserId(userId);
        return sessions.stream()
                .map(ChatSessionDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取会话的消息记录
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 消息列表
     */
    @Override
    public List<ChatMessageDTO> getMessages(String sessionId, Long userId) {
        // 验证会话归属
        ChatSession session = chatSessionMapper.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));
        
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该会话");
        }
        
        List<ChatMessage> messages = chatMessageMapper.findBySessionId(sessionId);
        return messages.stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 发送消息并获取AI回复
     *
     * @param request 消息请求
     * @param userId  用户ID
     * @return AI回复
     */
    @Override
    @Transactional
    public AiMessageResponse sendMessage(SendMessageRequest request, Long userId) {
        String sessionId = request.getSessionId();
        String userMessage = request.getContent();
        
        // 验证会话归属
        ChatSession session = chatSessionMapper.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));
        
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该会话");
        }
        
        // 分析风险等级
        String riskLevel = aiUtil.analyzeRiskLevel(userMessage);
        
        // 保存用户消息
        ChatMessage userChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role("user")
                .content(userMessage)
                .type(request.getType())
                .riskLevel(riskLevel)
                .build();
        chatMessageMapper.insert(userChatMessage);
        
        // 调用AI获取回复
        String aiReply = aiUtil.sendMessage(userMessage);
        
        // 保存AI回复
        ChatMessage aiChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role("assistant")
                .content(aiReply)
                .type("text")
                .riskLevel(riskLevel)
                .build();
        chatMessageMapper.insert(aiChatMessage);
        
        // 更新会话的最后消息
        session.setLastMessage(userMessage.length() > 50 ? 
                userMessage.substring(0, 50) + "..." : userMessage);
        
        // 如果是第一条消息，更新会话标题
        List<ChatMessage> allMessages = chatMessageMapper.findBySessionId(sessionId);
        if (allMessages.size() <= 2) { // 用户消息 + AI回复 = 2条
            String title = userMessage.length() > 20 ? 
                    userMessage.substring(0, 20) + "..." : userMessage;
            session.setTitle(title);
        }
        
        chatSessionMapper.update(session);
        
        // 获取建议操作
        List<String> suggestedActions = aiUtil.getSuggestedActions(riskLevel);
        
        log.info("AI对话完成，sessionId: {}, riskLevel: {}", sessionId, riskLevel);
        
        return AiMessageResponse.builder()
                .reply(aiReply)
                .riskLevel(riskLevel)
                .suggestedActions(suggestedActions)
                .build();
    }
    
    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     */
    @Override
    @Transactional
    public void deleteSession(String sessionId, Long userId) {
        // 验证会话归属
        ChatSession session = chatSessionMapper.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));
        
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该会话");
        }
        
        // 软删除会话
        chatSessionMapper.softDelete(sessionId);
        
        log.info("删除会话成功，sessionId: {}", sessionId);
    }
}
