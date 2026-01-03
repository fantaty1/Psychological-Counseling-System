package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 完成预约请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteAppointmentRequest {
    
    /**
     * 咨询备注
     */
    private String notes;
}
