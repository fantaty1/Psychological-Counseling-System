package com.cqust.psychologicalcounseling.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公告实体类
 * 对应数据库表：announcement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    
    /**
     * 公告ID，主键
     */
    private Long id;
    
    /**
     * 公告标题
     */
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
