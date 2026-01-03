package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.AssessmentRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户测评记录Mapper
 */
@Mapper
public interface AssessmentRecordMapper {
    
    /**
     * 创建测评记录
     */
    @Insert("INSERT INTO assessment_record (user_id, scale_id, status, started_at) " +
            "VALUES (#{userId}, #{scaleId}, #{status}, #{startedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AssessmentRecord record);
    
    /**
     * 更新测评记录
     */
    @Update("UPDATE assessment_record SET total_score = #{totalScore}, result_level = #{resultLevel}, " +
            "result_summary = #{resultSummary}, duration = #{duration}, status = #{status}, " +
            "completed_at = #{completedAt} WHERE id = #{id}")
    int update(AssessmentRecord record);
    
    /**
     * 根据ID获取测评记录
     */
    @Select("SELECT * FROM assessment_record WHERE id = #{id}")
    AssessmentRecord findById(@Param("id") Long id);
    
    /**
     * 获取用户的测评记录列表
     */
    @Select("SELECT * FROM assessment_record WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<AssessmentRecord> findByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户某量表的最新记录
     */
    @Select("SELECT * FROM assessment_record WHERE user_id = #{userId} AND scale_id = #{scaleId} " +
            "ORDER BY created_at DESC LIMIT 1")
    AssessmentRecord findLatestByUserAndScale(@Param("userId") Long userId, @Param("scaleId") Long scaleId);
    
    /**
     * 获取用户进行中的测评记录
     */
    @Select("SELECT * FROM assessment_record WHERE user_id = #{userId} AND scale_id = #{scaleId} " +
            "AND status = 'in_progress' ORDER BY created_at DESC LIMIT 1")
    AssessmentRecord findInProgressByUserAndScale(@Param("userId") Long userId, @Param("scaleId") Long scaleId);
    
    /**
     * 获取用户已完成的测评数量
     */
    @Select("SELECT COUNT(*) FROM assessment_record WHERE user_id = #{userId} AND status = 'completed'")
    int countCompletedByUserId(@Param("userId") Long userId);
}
