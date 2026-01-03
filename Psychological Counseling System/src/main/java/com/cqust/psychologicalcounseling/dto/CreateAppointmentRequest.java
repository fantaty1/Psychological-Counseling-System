package com.cqust.psychologicalcounseling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建预约请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    
    /**
     * 咨询师ID
     */
    @NotNull(message = "咨询师ID不能为空")
    private Long doctorId;
    
    /**
     * 预约日期（格式：yyyy-MM-dd）
     */
    @NotBlank(message = "预约日期不能为空")
    private String date;
    
    /**
     * 时间段（如：14:00）
     */
    @NotBlank(message = "时间段不能为空")
    private String timeSlot;
    
    /**
     * 咨询方式：offline-线下 online-线上
     */
    @NotBlank(message = "咨询方式不能为空")
    private String type;
    
    /**
     * 问题描述
     */
    private String description;
}
