package com.cqust.psychologicalcounseling.controller;

import com.cqust.psychologicalcounseling.common.ApiResponse;
import com.cqust.psychologicalcounseling.dto.AssessmentResultDTO;
import com.cqust.psychologicalcounseling.dto.SubmitAssessmentDTO;
import com.cqust.psychologicalcounseling.entity.AssessmentQuestion;
import com.cqust.psychologicalcounseling.entity.AssessmentRecord;
import com.cqust.psychologicalcounseling.entity.AssessmentScale;
import com.cqust.psychologicalcounseling.service.AssessmentService;
import com.cqust.psychologicalcounseling.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 心理测评控制器
 */
@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {
    
    private final AssessmentService assessmentService;
    private final JwtUtil jwtUtil;
    
    /**
     * 从请求中获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
    
    /**
     * 获取所有量表列表
     */
    @GetMapping("/scales")
    public ApiResponse<List<AssessmentScale>> getScales() {
        List<AssessmentScale> scales = assessmentService.getAllScales();
        return ApiResponse.success(scales);
    }
    
    /**
     * 获取量表详情（包含题目）
     */
    @GetMapping("/scales/{scaleId}")
    public ApiResponse<Map<String, Object>> getScaleDetail(@PathVariable Long scaleId) {
        AssessmentScale scale = assessmentService.getScaleById(scaleId);
        if (scale == null) {
            return ApiResponse.error("量表不存在");
        }
        
        List<AssessmentQuestion> questions = assessmentService.getQuestionsByScaleId(scaleId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("scale", scale);
        data.put("questions", questions);
        
        return ApiResponse.success(data);
    }
    
    /**
     * 获取量表的题目列表
     */
    @GetMapping("/scales/{scaleId}/questions")
    public ApiResponse<List<AssessmentQuestion>> getQuestions(@PathVariable Long scaleId) {
        List<AssessmentQuestion> questions = assessmentService.getQuestionsByScaleId(scaleId);
        return ApiResponse.success(questions);
    }
    
    /**
     * 提交测评答案
     */
    @PostMapping("/submit")
    public ApiResponse<AssessmentResultDTO> submitAssessment(
            @RequestBody SubmitAssessmentDTO dto,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        try {
            AssessmentResultDTO result = assessmentService.submitAssessment(userId, dto);
            return ApiResponse.success(result, "测评提交成功");
        } catch (Exception e) {
            return ApiResponse.error("提交失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的测评记录列表
     */
    @GetMapping("/records")
    public ApiResponse<List<AssessmentRecord>> getRecords(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        List<AssessmentRecord> records = assessmentService.getUserRecords(userId);
        return ApiResponse.success(records);
    }
    
    /**
     * 获取测评结果详情
     */
    @GetMapping("/results/{recordId}")
    public ApiResponse<AssessmentResultDTO> getResult(@PathVariable Long recordId) {
        AssessmentResultDTO result = assessmentService.getResultById(recordId);
        if (result == null) {
            return ApiResponse.error("记录不存在");
        }
        return ApiResponse.success(result);
    }
    
    /**
     * 获取用户测评统计
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.error("请先登录");
        }
        
        int completedCount = assessmentService.getCompletedCount(userId);
        List<AssessmentScale> scales = assessmentService.getAllScales();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("completedCount", completedCount);
        stats.put("totalScales", scales.size());
        
        return ApiResponse.success(stats);
    }
}
