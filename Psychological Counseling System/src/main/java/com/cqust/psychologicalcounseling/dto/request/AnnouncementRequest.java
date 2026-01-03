package com.cqust.psychologicalcounseling.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 创建/更新公告请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {
    
    /**
     * 公告ID（更新时使用）
     */
    private Long id;
    
    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 发布日期
     */
    private LocalDate date;
    
    /**
     * 状态：0-下架 1-发布
     */
    private Integer status;
    
    /**
     * 排序权重
     */
    private Integer sort;
}
