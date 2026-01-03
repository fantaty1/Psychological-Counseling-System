package com.cqust.psychologicalcounseling.dto.response;

import com.cqust.psychologicalcounseling.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT token
     */
    private String token;
    
    /**
     * 用户信息（通用字段）
     */
    private Object user;
    
    /**
     * 角色类型：student-学生, doctor-咨询师
     */
    private String role;
}
