package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.AssessmentAnswer;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户答题记录Mapper
 */
@Mapper
public interface AssessmentAnswerMapper {
    
    /**
     * 批量插入答案
     */
    @Insert("<script>" +
            "INSERT INTO assessment_answer (record_id, question_id, answer_value, score) VALUES " +
            "<foreach collection='answers' item='answer' separator=','>" +
            "(#{answer.recordId}, #{answer.questionId}, #{answer.answerValue}, #{answer.score})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("answers") List<AssessmentAnswer> answers);
    
    /**
     * 插入单个答案
     */
    @Insert("INSERT INTO assessment_answer (record_id, question_id, answer_value, score) " +
            "VALUES (#{recordId}, #{questionId}, #{answerValue}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AssessmentAnswer answer);
    
    /**
     * 根据记录ID获取所有答案
     */
    @Select("SELECT * FROM assessment_answer WHERE record_id = #{recordId} ORDER BY id ASC")
    List<AssessmentAnswer> findByRecordId(@Param("recordId") Long recordId);
    
    /**
     * 计算记录的总分
     */
    @Select("SELECT COALESCE(SUM(score), 0) FROM assessment_answer WHERE record_id = #{recordId}")
    int sumScoreByRecordId(@Param("recordId") Long recordId);
    
    /**
     * 删除记录的所有答案
     */
    @Delete("DELETE FROM assessment_answer WHERE record_id = #{recordId}")
    int deleteByRecordId(@Param("recordId") Long recordId);
}
