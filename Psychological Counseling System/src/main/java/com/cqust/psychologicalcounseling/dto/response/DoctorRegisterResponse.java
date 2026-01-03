package com.cqust.psychologicalcounseling.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 咨询师注册响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterResponse {
    
    /**
     * 咨询师ID
     */
    private Long doctorId;
    
    /**
     * 咨询师姓名
     */
    private String name;
}
