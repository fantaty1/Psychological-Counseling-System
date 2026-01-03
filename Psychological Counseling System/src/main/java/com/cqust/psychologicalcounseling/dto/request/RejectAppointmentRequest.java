package com.cqust.psychologicalcounseling.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拒绝预约请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectAppointmentRequest {
    
    /**
     * 拒绝原因
     */
    private String reason;
}
