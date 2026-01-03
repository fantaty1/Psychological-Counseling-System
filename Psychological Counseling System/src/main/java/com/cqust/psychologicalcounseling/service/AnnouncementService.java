package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.AnnouncementDTO;
import com.cqust.psychologicalcounseling.dto.request.AnnouncementRequest;

import java.util.List;

/**
 * 公告服务接口
 * 定义公告相关的业务操作
 */
public interface AnnouncementService {
    
    /**
     * 获取所有公告（管理员用）
     *
     * @return 公告列表
     */
    List<AnnouncementDTO> getAllAnnouncements();
    
    /**
     * 获取已发布的公告（学生端用）
     *
     * @return 公告列表
     */
    List<AnnouncementDTO> getPublishedAnnouncements();
    
    /**
     * 获取已发布的公告（限制数量）
     *
     * @param limit 数量限制
     * @return 公告列表
     */
    List<AnnouncementDTO> getPublishedAnnouncements(int limit);
    
    /**
     * 获取公告详情
     *
     * @param id 公告ID
     * @return 公告信息
     */
    AnnouncementDTO getAnnouncementById(Long id);
    
    /**
     * 创建公告
     *
     * @param request 创建请求
     * @return 公告信息
     */
    AnnouncementDTO createAnnouncement(AnnouncementRequest request);
    
    /**
     * 更新公告
     *
     * @param request 更新请求
     * @return 公告信息
     */
    AnnouncementDTO updateAnnouncement(AnnouncementRequest request);
    
    /**
     * 更新公告状态
     *
     * @param id 公告ID
     * @param status 状态（0-下架 1-发布）
     */
    void updateAnnouncementStatus(Long id, Integer status);
    
    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void deleteAnnouncement(Long id);
}
