package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.AssessmentQuestion;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 测评题目Mapper
 */
@Mapper
public interface AssessmentQuestionMapper {
    
    /**
     * 根据量表ID获取所有题目
     */
    @Select("SELECT * FROM assessment_question WHERE scale_id = #{scaleId} ORDER BY question_no ASC")
    List<AssessmentQuestion> findByScaleId(@Param("scaleId") Long scaleId);
    
    /**
     * 根据ID获取题目
     */
    @Select("SELECT * FROM assessment_question WHERE id = #{id}")
    AssessmentQuestion findById(@Param("id") Long id);
    
    /**
     * 获取量表的题目数量
     */
    @Select("SELECT COUNT(*) FROM assessment_question WHERE scale_id = #{scaleId}")
    int countByScaleId(@Param("scaleId") Long scaleId);
}
