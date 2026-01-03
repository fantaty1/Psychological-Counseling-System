package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新咨询师个人信息请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDoctorProfileRequest {
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 个人简介
     */
    private String description;
    
    /**
     * 诊室位置
     */
    private String location;
    
    /**
     * 从业年限
     */
    private Integer years;
    
    /**
     * 擅长领域标签
     */
    private List<String> tags;
    
    /**
     * 咨询方法
     */
    private List<String> methods;
}
