package com.cqust.psychologicalcounseling.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 咨询师注册请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterRequest {
    
    /**
     * 咨询师姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度应为2-20个字符")
    private String name;
    
    /**
     * 职称
     */
    @NotBlank(message = "职称不能为空")
    @Size(max = 50, message = "职称长度不能超过50个字符")
    private String title;
    
    /**
     * 手机号码（用于登录）
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应为6-20个字符")
    private String password;
    
    /**
     * 电子邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 从业年限
     */
    @NotNull(message = "从业年限不能为空")
    @Min(value = 0, message = "从业年限不能为负数")
    @Max(value = 50, message = "从业年限不能超过50年")
    private Integer years;
    
    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String description;
    
    /**
     * 擅长领域标签
     */
    private List<String> tags;
    
    /**
     * 咨询方法
     */
    private List<String> methods;
    
    /**
     * 资质认证
     */
    private List<String> certifications;
}
