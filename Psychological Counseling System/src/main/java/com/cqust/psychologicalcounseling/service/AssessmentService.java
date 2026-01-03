package com.cqust.psychologicalcounseling.service;

import com.cqust.psychologicalcounseling.dto.AssessmentResultDTO;
import com.cqust.psychologicalcounseling.dto.SubmitAssessmentDTO;
import com.cqust.psychologicalcounseling.entity.AssessmentQuestion;
import com.cqust.psychologicalcounseling.entity.AssessmentRecord;
import com.cqust.psychologicalcounseling.entity.AssessmentScale;

import java.util.List;

/**
 * 心理测评服务接口
 */
public interface AssessmentService {
    
    /**
     * 获取所有启用的量表列表
     */
    List<AssessmentScale> getAllScales();
    
    /**
     * 根据ID获取量表
     */
    AssessmentScale getScaleById(Long scaleId);
    
    /**
     * 获取量表的所有题目
     */
    List<AssessmentQuestion> getQuestionsByScaleId(Long scaleId);
    
    /**
     * 提交测评答案
     */
    AssessmentResultDTO submitAssessment(Long userId, SubmitAssessmentDTO dto);
    
    /**
     * 获取用户的测评记录列表
     */
    List<AssessmentRecord> getUserRecords(Long userId);
    
    /**
     * 获取测评结果详情
     */
    AssessmentResultDTO getResultById(Long recordId);
    
    /**
     * 获取用户已完成的测评数量
     */
    int getCompletedCount(Long userId);
}
