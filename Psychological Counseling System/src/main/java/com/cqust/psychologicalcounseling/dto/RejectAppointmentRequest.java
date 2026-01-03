package com.cqust.psychologicalcounseling.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拒绝预约请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectAppointmentRequest {
    
    /**
     * 拒绝原因
     */
    @NotBlank(message = "拒绝原因不能为空")
    private String reason;
}
