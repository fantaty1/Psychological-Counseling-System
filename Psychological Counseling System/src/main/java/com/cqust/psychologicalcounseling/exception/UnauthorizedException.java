package com.cqust.psychologicalcounseling.exception;

/**
 * 未授权异常
 * 用于处理未登录或token无效的情况
 */
public class UnauthorizedException extends RuntimeException {
    
    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public UnauthorizedException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
