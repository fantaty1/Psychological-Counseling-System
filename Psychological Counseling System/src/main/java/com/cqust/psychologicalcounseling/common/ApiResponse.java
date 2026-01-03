package com.cqust.psychologicalcounseling.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应类
 * 用于封装所有API接口的响应数据
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 错误码，0表示成功
     */
    private Integer error;
    
    /**
     * 响应数据
     */
    private T body;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 成功响应（带数据和消息）
     *
     * @param data    响应数据
     * @param message 响应消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(0, data, message, true);
    }
    
    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, data, "操作成功", true);
    }
    
    /**
     * 成功响应（仅消息）
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(0, null, message, true);
    }
    
    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(0, null, "操作成功", true);
    }
    
    /**
     * 失败响应
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param <T>       数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(Integer errorCode, String message) {
        return new ApiResponse<>(errorCode, null, message, false);
    }
    
    /**
     * 失败响应（默认错误码为1）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(1, null, message, false);
    }
}
