package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 公告数据访问层
 * 负责公告表的CRUD操作
 */
@Mapper
public interface AnnouncementMapper {
    
    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    @Select("SELECT * FROM announcement WHERE id = #{id}")
    Optional<Announcement> findById(@Param("id") Long id);
    
    /**
     * 查询所有公告（按日期排序）
     *
     * @return 公告列表
     */
    @Select("SELECT * FROM announcement ORDER BY sort DESC, date DESC")
    List<Announcement> findAll();
    
    /**
     * 查询已发布的公告（状态为1）
     *
     * @return 公告列表
     */
    @Select("SELECT * FROM announcement WHERE status = 1 ORDER BY sort DESC, date DESC")
    List<Announcement> findPublished();
    
    /**
     * 查询已发布的公告（限制数量）
     *
     * @param limit 数量限制
     * @return 公告列表
     */
    @Select("SELECT * FROM announcement WHERE status = 1 ORDER BY sort DESC, date DESC LIMIT #{limit}")
    List<Announcement> findPublishedWithLimit(@Param("limit") int limit);
    
    /**
     * 插入新公告
     *
     * @param announcement 公告信息
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO announcement (
            title, content, date, status, sort, created_at, updated_at
        ) VALUES (
            #{title}, #{content}, #{date}, #{status}, #{sort}, #{createdAt}, #{updatedAt}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);
    
    /**
     * 更新公告
     *
     * @param announcement 公告信息
     * @return 影响行数
     */
    @Update("""
        UPDATE announcement SET
            title = #{title},
            content = #{content},
            date = #{date},
            status = #{status},
            sort = #{sort},
            updated_at = #{updatedAt}
        WHERE id = #{id}
    """)
    int update(Announcement announcement);
    
    /**
     * 更新公告状态
     *
     * @param id 公告ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE announcement SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 影响行数
     */
    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
