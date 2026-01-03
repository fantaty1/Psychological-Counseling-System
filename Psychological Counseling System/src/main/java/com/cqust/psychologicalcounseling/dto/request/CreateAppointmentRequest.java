package com.cqust.psychologicalcounseling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建预约请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    
    /**
     * 医生ID
     */
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;
    
    /**
     * 预约日期
     */
    @NotBlank(message = "预约日期不能为空")
    private String date;
    
    /**
     * 时间段
     */
    @NotBlank(message = "时间段不能为空")
    private String timeSlot;
    
    /**
     * 咨询方式 offline/online
     */
    @NotBlank(message = "咨询方式不能为空")
    private String type;
    
    /**
     * 咨询描述
     */
    private String description;
}
