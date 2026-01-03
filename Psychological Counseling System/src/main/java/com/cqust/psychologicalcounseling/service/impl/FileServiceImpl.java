package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.exception.BusinessException;
import com.cqust.psychologicalcounseling.mapper.DoctorMapper;
import com.cqust.psychologicalcounseling.mapper.UserMapper;
import com.cqust.psychologicalcounseling.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    
    // 上传文件保存目录（相对于项目根目录）
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    // 允许的图片类型
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // 最大文件大小 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    @Override
    public String uploadAvatar(MultipartFile file, Long userId, String role) {
        // 验证文件
        validateImageFile(file);
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;
        
        // 根据角色保存到不同文件夹：学生保存到 avatars/students/，医生保存到 avatars/doctors/
        String subDir = "doctor".equals(role) ? "doctors" : "students";
        String relativePath = "avatars/" + subDir + "/" + newFilename;
        saveFile(file, relativePath);
        
        // 生成访问URL
        String avatarUrl = "/uploads/" + relativePath;
        
        // 更新数据库中的头像URL
        if ("doctor".equals(role)) {
            doctorMapper.updateAvatar(userId, avatarUrl);
        } else {
            userMapper.updateAvatar(userId, avatarUrl);
        }
        
        log.info("头像上传成功，用户ID: {}, 角色: {}, URL: {}", userId, role, avatarUrl);
        return avatarUrl;
    }
    
    @Override
    public String uploadFile(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        String relativePath = type + "/" + newFilename;
        saveFile(file, relativePath);
        
        // 生成访问URL
        String fileUrl = "/uploads/" + relativePath;
        
        log.info("文件上传成功，类型: {}, URL: {}", type, fileUrl);
        return fileUrl;
    }
    
    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过5MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("只支持 JPG、PNG、GIF、WEBP 格式的图片");
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    /**
     * 保存文件到磁盘
     */
    private void saveFile(MultipartFile file, String relativePath) {
        try {
            // 构建完整路径
            Path uploadPath = Paths.get(uploadDir, relativePath);
            
            // 确保目录存在
            Files.createDirectories(uploadPath.getParent());
            
            // 保存文件
            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件保存成功: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException("文件保存失败，请稍后重试");
        }
    }
}
