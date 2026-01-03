package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 咨询师实体类
 * 对应数据库表：doctor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    
    /**
     * 咨询师ID，主键
     */
    private Long id;
    
    /**
     * 咨询师姓名
     */
    private String name;
    
    /**
     * 职称（如：注册心理师、国家二级心理咨询师等）
     */
    private String title;
    
    /**
     * 手机号码（唯一，用于登录）
     */
    private String phone;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 头像URL
     */
    private String avatar;
    
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
     * 擅长领域标签（JSON数组格式，如：["焦虑抑郁","个人成长"]）
     */
    private String tags;
    
    /**
     * 咨询方法（JSON数组格式，如：["认知行为疗法","人本主义"]）
     */
    private String methods;
    
    /**
     * 资质认证（JSON数组格式，如：["中国心理学会注册心理师 (X-21-001)"]）
     */
    private String certifications;
    
    /**
     * 评分（1.0-5.0）
     */
    private BigDecimal rating;
    
    /**
     * 累计咨询时长（小时）
     */
    private BigDecimal totalHours;
    
    /**
     * 累计帮助人数
     */
    private Integer helpedCount;
    
    /**
     * 好评率（0-100）
     */
    private Integer positiveRate;
    
    /**
     * 是否可预约：0-不可预约 1-可预约
     */
    private Integer available;
    
    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
