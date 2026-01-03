package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.AnnouncementDTO;
import com.cqust.psychologicalcounseling.dto.request.AnnouncementRequest;
import com.cqust.psychologicalcounseling.entity.Announcement;
import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.AnnouncementMapper;
import com.cqust.psychologicalcounseling.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    
    private final AnnouncementMapper announcementMapper;
    
    /**
     * 获取所有公告（管理员用）
     */
    @Override
    public List<AnnouncementDTO> getAllAnnouncements() {
        List<Announcement> announcements = announcementMapper.findAll();
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取已发布的公告
     */
    @Override
    public List<AnnouncementDTO> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementMapper.findPublished();
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取已发布的公告（限制数量）
     */
    @Override
    public List<AnnouncementDTO> getPublishedAnnouncements(int limit) {
        List<Announcement> announcements = announcementMapper.findPublishedWithLimit(limit);
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取公告详情
     */
    @Override
    public AnnouncementDTO getAnnouncementById(Long id) {
        Announcement announcement = announcementMapper.findById(id)
                .orElseThrow(() -> new BusinessException("公告不存在"));
        return convertToDTO(announcement);
    }
    
    /**
     * 创建公告
     */
    @Override
    @Transactional
    public AnnouncementDTO createAnnouncement(AnnouncementRequest request) {
        log.info("创建公告: {}", request.getTitle());
        
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate() != null ? request.getDate() : LocalDate.now())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .sort(request.getSort() != null ? request.getSort() : 0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        announcementMapper.insert(announcement);
        log.info("公告创建成功，ID: {}", announcement.getId());
        
        return convertToDTO(announcement);
    }
    
    /**
     * 更新公告
     */
    @Override
    @Transactional
    public AnnouncementDTO updateAnnouncement(AnnouncementRequest request) {
        log.info("更新公告，ID: {}", request.getId());
        
        if (request.getId() == null) {
            throw new BusinessException("公告ID不能为空");
        }
        
        Announcement existing = announcementMapper.findById(request.getId())
                .orElseThrow(() -> new BusinessException("公告不存在"));
        
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        if (request.getDate() != null) {
            existing.setDate(request.getDate());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getSort() != null) {
            existing.setSort(request.getSort());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        
        announcementMapper.update(existing);
        log.info("公告更新成功，ID: {}", existing.getId());
        
        return convertToDTO(existing);
    }
    
    /**
     * 更新公告状态
     */
    @Override
    @Transactional
    public void updateAnnouncementStatus(Long id, Integer status) {
        log.info("更新公告状态，ID: {}, 状态: {}", id, status);
        int rows = announcementMapper.updateStatus(id, status);
        if (rows == 0) {
            throw new BusinessException("公告不存在");
        }
    }
    
    /**
     * 删除公告
     */
    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        log.info("删除公告，ID: {}", id);
        int rows = announcementMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException("公告不存在");
        }
    }
    
    /**
     * 转换为DTO
     */
    private AnnouncementDTO convertToDTO(Announcement announcement) {
        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .date(announcement.getDate())
                .status(announcement.getStatus())
                .sort(announcement.getSort())
                .createdAt(announcement.getCreatedAt())
                .build();
    }
}
