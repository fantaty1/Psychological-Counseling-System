package com.cqust.psychologicalcounseling.dto.response;

import com.cqust.psychologicalcounseling.dto.AdminDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * 管理员信息
     */
    private AdminDTO admin;
    
    /**
     * 角色
     */
    private String role;
}
