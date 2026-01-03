package com.cqust.psychologicalcounseling.util;

import com.cqust.psychologicalcounseling.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户上下文工具类
 * 用于从请求中获取当前登录用户信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserContext {
    
    private final JwtUtil jwtUtil;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * 从请求中获取当前用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            throw new UnauthorizedException("请先登录");
        }
        
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("登录已过期，请重新登录");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new UnauthorizedException("无效的登录凭证");
        }
        
        return userId;
    }
    
    /**
     * 尝试从请求中获取当前用户ID（不抛出异常）
     *
     * @param request HTTP请求
     * @return 用户ID，如果未登录返回null
     */
    public Long tryGetCurrentUserId(HttpServletRequest request) {
        try {
            return getCurrentUserId(request);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从请求头中提取token
     *
     * @param request HTTP请求
     * @return token字符串，如果不存在返回null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    
    /**
     * 从请求中获取当前用户角色
     *
     * @param request HTTP请求
     * @return 角色（student/doctor）
     */
    public String getCurrentRole(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            log.warn("获取角色时token为空，默认返回student");
            return "student";
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        log.info("从token获取的username: {}", username);
        
        if (username != null && username.startsWith("doctor:")) {
            log.info("识别为医生角色");
            return "doctor";
        }
        log.info("识别为学生角色");
        return "student";
    }
}
