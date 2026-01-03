package com.cqust.psychologicalcounseling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 预约列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListDTO {
    
    /**
     * 预约列表
     */
    private List<AppointmentDTO> list;
    
    /**
     * 总数
     */
    private Integer total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
}
