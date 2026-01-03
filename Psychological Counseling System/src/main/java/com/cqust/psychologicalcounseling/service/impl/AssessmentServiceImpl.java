package com.cqust.psychologicalcounseling.service.impl;

import com.cqust.psychologicalcounseling.dto.AssessmentResultDTO;
import com.cqust.psychologicalcounseling.dto.SubmitAssessmentDTO;
import com.cqust.psychologicalcounseling.entity.*;
import com.cqust.psychologicalcounseling.mapper.*;
import com.cqust.psychologicalcounseling.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 心理测评服务实现类
 */
@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {
    
    private final AssessmentScaleMapper scaleMapper;
    private final AssessmentQuestionMapper questionMapper;
    private final AssessmentRecordMapper recordMapper;
    private final AssessmentAnswerMapper answerMapper;
    
    @Override
    public List<AssessmentScale> getAllScales() {
        return scaleMapper.findAllEnabled();
    }
    
    @Override
    public AssessmentScale getScaleById(Long scaleId) {
        return scaleMapper.findById(scaleId);
    }
    
    @Override
    public List<AssessmentQuestion> getQuestionsByScaleId(Long scaleId) {
        return questionMapper.findByScaleId(scaleId);
    }
    
    @Override
    @Transactional
    public AssessmentResultDTO submitAssessment(Long userId, SubmitAssessmentDTO dto) {
        // 1. 获取量表信息
        AssessmentScale scale = scaleMapper.findById(dto.getScaleId());
        if (scale == null) {
            throw new RuntimeException("量表不存在");
        }
        
        // 2. 获取题目信息用于计分
        List<AssessmentQuestion> questions = questionMapper.findByScaleId(dto.getScaleId());
        Map<Long, AssessmentQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(AssessmentQuestion::getId, q -> q));
        
        // 3. 创建测评记录
        AssessmentRecord record = new AssessmentRecord();
        record.setUserId(userId);
        record.setScaleId(dto.getScaleId());
        record.setStatus("in_progress");
        record.setStartedAt(LocalDateTime.now().minusSeconds(dto.getDuration() != null ? dto.getDuration() : 0));
        recordMapper.insert(record);
        
        // 4. 保存答案并计算分数
        List<AssessmentAnswer> answers = new ArrayList<>();
        int totalScore = 0;
        Map<String, List<Integer>> dimensionScores = new HashMap<>();
        
        for (SubmitAssessmentDTO.AnswerItem item : dto.getAnswers()) {
            AssessmentQuestion question = questionMap.get(item.getQuestionId());
            if (question == null) continue;
            
            // 计算得分（考虑反向计分）
            int score = item.getAnswerValue();
            if (question.getReverseScore() != null && question.getReverseScore() == 1) {
                // 反向计分：假设选项值为0-4，则反向为4-0
                score = 4 - item.getAnswerValue();
            }
            
            totalScore += score;
            
            // 按维度统计
            if (question.getDimension() != null) {
                dimensionScores.computeIfAbsent(question.getDimension(), k -> new ArrayList<>()).add(score);
            }
            
            AssessmentAnswer answer = new AssessmentAnswer();
            answer.setRecordId(record.getId());
            answer.setQuestionId(item.getQuestionId());
            answer.setAnswerValue(item.getAnswerValue());
            answer.setScore(score);
            answers.add(answer);
        }
        
        // 5. 批量保存答案
        if (!answers.isEmpty()) {
            answerMapper.batchInsert(answers);
        }
        
        // 6. 计算结果等级和摘要
        String resultLevel = calculateResultLevel(totalScore, questions.size());
        String resultSummary = generateResultSummary(resultLevel, totalScore);
        
        // 7. 更新测评记录
        record.setTotalScore(totalScore);
        record.setResultLevel(resultLevel);
        record.setResultSummary(resultSummary);
        record.setDuration(dto.getDuration());
        record.setStatus("completed");
        record.setCompletedAt(LocalDateTime.now());
        recordMapper.update(record);
        
        // 8. 构建返回结果
        AssessmentResultDTO result = new AssessmentResultDTO();
        result.setRecordId(record.getId());
        result.setScaleId(scale.getId());
        result.setScaleName(scale.getName());
        result.setTotalScore(totalScore);
        result.setResultLevel(resultLevel);
        result.setResultSummary(resultSummary);
        result.setDuration(dto.getDuration());
        result.setCompletedAt(record.getCompletedAt());
        
        // 添加维度分析
        List<AssessmentResultDTO.DimensionScore> dimensions = dimensionScores.entrySet().stream()
                .map(entry -> {
                    AssessmentResultDTO.DimensionScore ds = new AssessmentResultDTO.DimensionScore();
                    ds.setDimension(entry.getKey());
                    ds.setScore(entry.getValue().stream().mapToInt(Integer::intValue).sum());
                    ds.setCount(entry.getValue().size());
                    ds.setAverage(entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0));
                    return ds;
                })
                .sorted(Comparator.comparing(AssessmentResultDTO.DimensionScore::getDimension))
                .collect(Collectors.toList());
        result.setDimensions(dimensions);
        
        return result;
    }
    
    @Override
    public List<AssessmentRecord> getUserRecords(Long userId) {
        return recordMapper.findByUserId(userId);
    }
    
    @Override
    public AssessmentResultDTO getResultById(Long recordId) {
        AssessmentRecord record = recordMapper.findById(recordId);
        if (record == null) {
            return null;
        }
        
        AssessmentScale scale = scaleMapper.findById(record.getScaleId());
        
        AssessmentResultDTO result = new AssessmentResultDTO();
        result.setRecordId(record.getId());
        result.setScaleId(record.getScaleId());
        result.setScaleName(scale != null ? scale.getName() : "未知量表");
        result.setTotalScore(record.getTotalScore());
        result.setResultLevel(record.getResultLevel());
        result.setResultSummary(record.getResultSummary());
        result.setDuration(record.getDuration());
        result.setCompletedAt(record.getCompletedAt());
        
        return result;
    }
    
    @Override
    public int getCompletedCount(Long userId) {
        return recordMapper.countCompletedByUserId(userId);
    }
    
    /**
     * 根据总分计算结果等级
     * 50题量表评分标准（总分范围0-200）：
     * 总分 < 90：正常
     * 90-110：轻度
     * 110-140：中度
     * >= 140：重度
     * 
     * 评分按题目数量自动调整阈值，适配不同长度的量表
     */
    private String calculateResultLevel(int totalScore, int questionCount) {
        // 按题目数量调整阈值（以50题为基准）
        double factor = questionCount / 50.0;
        
        int normalThreshold = (int) (90 * factor);
        int mildThreshold = (int) (110 * factor);
        int moderateThreshold = (int) (140 * factor);
        
        if (totalScore < normalThreshold) {
            return "正常";
        } else if (totalScore < mildThreshold) {
            return "轻度";
        } else if (totalScore < moderateThreshold) {
            return "中度";
        } else {
            return "重度";
        }
    }
    
    /**
     * 生成结果摘要
     */
    private String generateResultSummary(String resultLevel, int totalScore) {
        return switch (resultLevel) {
            case "正常" -> "您的心理健康状况良好，各项指标均在正常范围内。请继续保持积极健康的生活方式。";
            case "轻度" -> "您目前可能存在轻微的心理压力或困扰。建议适当放松，如有需要可以寻求心理咨询帮助。";
            case "中度" -> "您目前可能面临一定的心理困扰。建议您寻求专业的心理咨询服务，以获得更好的支持和帮助。";
            case "重度" -> "您目前可能正在经历较大的心理困扰。强烈建议您尽快寻求专业心理医生的帮助。";
            default -> "测评完成，总分：" + totalScore;
        };
    }
}
