package com.cqust.psychologicalcounseling.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 完成预约请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteAppointmentRequest {
    
    /**
     * 咨询记录备注
     */
    private String notes;
}
