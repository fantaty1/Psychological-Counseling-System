package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类（学生）
 * 对应数据库表：user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID，主键
     */
    private Long id;
    
    /**
     * 用户名/姓名
     */
    private String username;
    
    /**
     * 学号（唯一）
     */
    private String studentId;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 学院
     */
    private String college;
    
    /**
     * 专业班级
     */
    private String majorClass;
    
    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;
    
    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 连续签到天数
     */
    private Integer checkinStreak;
    
    /**
     * 累计咨询时长（小时）
     */
    private BigDecimal counselingHours;
    
    /**
     * 累计预约次数
     */
    private Integer appointmentsCount;
    
    /**
     * 是否允许心理委员查看信息
     */
    private Integer allowPeerView;
    
    /**
     * 是否允许接收通知
     */
    private Integer allowNotification;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 删除时间（软删除）
     */
    private LocalDateTime deletedAt;
}
