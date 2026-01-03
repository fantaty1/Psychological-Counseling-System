package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 咨询师统计数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatsDTO {
    
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
}
