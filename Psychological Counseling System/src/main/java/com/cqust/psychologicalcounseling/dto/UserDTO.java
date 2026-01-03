package com.cqust.psychologicalcounseling.dto;

import com.cqust.psychologicalcounseling.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息DTO
 * 用于返回用户个人信息（不包含敏感信息）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名/姓名
     */
    private String name;
    
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
     * 学号
     */
    private String studentId;
    
    /**
     * 手机号码（脱敏）
     */
    private String phone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
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
     * 累计咨询时长
     */
    private java.math.BigDecimal counselingHours;
    
    /**
     * 累计预约次数
     */
    private Integer appointmentsCount;
    
    /**
     * 统计数据
     */
    private UserStatsDTO stats;
    
    /**
     * 从实体转换为DTO
     *
     * @param user 用户实体
     * @return UserDTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .avatar(user.getAvatar())
                .college(user.getCollege())
                .majorClass(user.getMajorClass())
                .studentId(user.getStudentId())
                .phone(maskPhone(user.getPhone()))
                .email(user.getEmail())
                .stats(UserStatsDTO.builder()
                        .counselingHours(user.getCounselingHours() != null ? 
                                user.getCounselingHours().doubleValue() : 0.0)
                        .appointmentsCount(user.getAppointmentsCount() != null ? 
                                user.getAppointmentsCount() : 0)
                        .checkinStreak(user.getCheckinStreak() != null ? 
                                user.getCheckinStreak() : 0)
                        .build())
                .build();
    }
    
    /**
     * 手机号脱敏
     *
     * @param phone 原始手机号
     * @return 脱敏后的手机号
     */
    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
