package com.cqust.psychologicalcounseling.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传头像
     *
     * @param file   头像文件
     * @param userId 用户ID
     * @param role   角色（student/doctor）
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file, Long userId, String role);
    
    /**
     * 上传通用文件
     *
     * @param file 文件
     * @param type 类型
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String type);
}
