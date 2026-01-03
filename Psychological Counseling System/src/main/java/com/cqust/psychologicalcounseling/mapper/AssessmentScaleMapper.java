package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.AssessmentScale;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 心理测评量表Mapper
 */
@Mapper
public interface AssessmentScaleMapper {
    
    /**
     * 获取所有启用的量表列表
     */
    @Select("SELECT * FROM assessment_scale WHERE status = 1 ORDER BY sort ASC, id ASC")
    List<AssessmentScale> findAllEnabled();
    
    /**
     * 根据ID获取量表
     */
    @Select("SELECT * FROM assessment_scale WHERE id = #{id}")
    AssessmentScale findById(@Param("id") Long id);
    
    /**
     * 根据分类获取量表
     */
    @Select("SELECT * FROM assessment_scale WHERE category = #{category} AND status = 1 ORDER BY sort ASC")
    List<AssessmentScale> findByCategory(@Param("category") String category);
    
    /**
     * 获取所有分类
     */
    @Select("SELECT DISTINCT category FROM assessment_scale WHERE status = 1 AND category IS NOT NULL")
    List<String> findAllCategories();
}
