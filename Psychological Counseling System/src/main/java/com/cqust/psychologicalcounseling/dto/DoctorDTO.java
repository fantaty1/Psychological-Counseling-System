package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 咨询师信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
        /**
         * 诊室位置
         */
        private String location;
    
    /**
     * 咨询师ID
     */
    private Long id;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 联系电话
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
     * 从业年限
     */
    private Integer years;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
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
    
    /**
     * 统计数据
     */
    private DoctorStatsDTO stats;
    
    /**
     * 是否可预约
     */
    private Boolean available;
    
    /**
     * 累计咨询时长
     */
    private BigDecimal totalHours;
    
    /**
     * 帮助人数
     */
    private Integer helpedCount;
    
    /**
     * 好评率
     */
    private Integer positiveRate;
    
    /**
     * 状态：0-待审核 1-正常 2-下架
     */
    private Integer status;
}
