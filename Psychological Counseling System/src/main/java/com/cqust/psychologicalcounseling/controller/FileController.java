package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.ApiResponse;
import com.cqust.psychologicalcounseling.service.FileService;
import com.cqust.psychologicalcounseling.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    private final UserContext userContext;
    
    /**
     * 上传头像
     * POST /api/file/avatar
     */
    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
        Long userId = userContext.getCurrentUserId(request);
        String role = userContext.getCurrentRole(request);
        log.info("用户上传头像，用户ID: {}, 角色: {}", userId, role);
        
        String avatarUrl = fileService.uploadAvatar(file, userId, role);
        return ApiResponse.success(Map.of("url", avatarUrl), "上传成功");
    }
    
    /**
     * 上传通用文件
     * POST /api/file/upload
     */
    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "common") String type) {
        log.info("上传文件，类型: {}, 文件名: {}", type, file.getOriginalFilename());
        
        String fileUrl = fileService.uploadFile(file, type);
        return ApiResponse.success(Map.of("url", fileUrl), "上传成功");
    }
}
