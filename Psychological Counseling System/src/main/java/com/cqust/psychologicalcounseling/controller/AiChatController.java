package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.Result;
import com.cqust.psychologicalcounseling.dto.ChatMessageDTO;
import com.cqust.psychologicalcounseling.dto.ChatSessionDTO;
import com.cqust.psychologicalcounseling.dto.request.SendMessageRequest;
import com.cqust.psychologicalcounseling.dto.response.AiMessageResponse;
import com.cqust.psychologicalcounseling.dto.response.CreateSessionResponse;
import com.cqust.psychologicalcounseling.service.AiChatService;
import com.cqust.psychologicalcounseling.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AiChatController {
    
    private final AiChatService aiChatService;
    private final UserContext userContext;
    
    /**
     * 创建新会话
     *
     * @param request HTTP请求
     * @return 会话信息
     */
    @PostMapping("/sessions")
    public Result<CreateSessionResponse> createSession(HttpServletRequest request) {
        Long userId = userContext.getCurrentUserId(request);
        CreateSessionResponse response = aiChatService.createSession(userId);
        return Result.success(response);
    }
    
    /**
     * 获取用户的会话列表
     *
     * @param request HTTP请求
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public Result<List<ChatSessionDTO>> getSessions(HttpServletRequest request) {
        Long userId = userContext.getCurrentUserId(request);
        List<ChatSessionDTO> sessions = aiChatService.getSessions(userId);
        return Result.success(sessions);
    }
    
    /**
     * 获取会话的消息记录
     *
     * @param sessionId 会话ID
     * @param request   HTTP请求
     * @return 消息列表
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ChatMessageDTO>> getMessages(
            @PathVariable String sessionId,
            HttpServletRequest request) {
        Long userId = userContext.getCurrentUserId(request);
        List<ChatMessageDTO> messages = aiChatService.getMessages(sessionId, userId);
        return Result.success(messages);
    }
    
    /**
     * 发送消息并获取AI回复
     *
     * @param sendRequest 消息请求
     * @param request     HTTP请求
     * @return AI回复
     */
    @PostMapping("/send")
    public Result<AiMessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest sendRequest,
            HttpServletRequest request) {
        Long userId = userContext.getCurrentUserId(request);
        AiMessageResponse response = aiChatService.sendMessage(sendRequest, userId);
        return Result.success(response);
    }
    
    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     * @param request   HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(
            @PathVariable String sessionId,
            HttpServletRequest request) {
        Long userId = userContext.getCurrentUserId(request);
        aiChatService.deleteSession(sessionId, userId);
        return Result.success();
    }
}
