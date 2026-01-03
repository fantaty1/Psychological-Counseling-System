package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.ChatSession;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * AI会话数据访问层
 */
@Mapper
public interface ChatSessionMapper {
    
    /**
     * 根据会话ID查询
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    @Select("SELECT * FROM chat_session WHERE session_id = #{sessionId} AND status = 1")
    Optional<ChatSession> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM chat_session WHERE user_id = #{userId} AND status = 1 ORDER BY updated_at DESC")
    List<ChatSession> findByUserId(@Param("userId") Long userId);
    
    /**
     * 插入新会话
     *
     * @param session 会话信息
     * @return 影响行数
     */
    @Insert("INSERT INTO chat_session (session_id, user_id, title, last_message, status, created_at, updated_at) " +
            "VALUES (#{sessionId}, #{userId}, #{title}, #{lastMessage}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatSession session);
    
    /**
     * 更新会话
     *
     * @param session 会话信息
     * @return 影响行数
     */
    @Update("UPDATE chat_session SET title = #{title}, last_message = #{lastMessage}, " +
            "updated_at = NOW() WHERE session_id = #{sessionId}")
    int update(ChatSession session);
    
    /**
     * 软删除会话
     *
     * @param sessionId 会话ID
     * @return 影响行数
     */
    @Update("UPDATE chat_session SET status = 0, updated_at = NOW() WHERE session_id = #{sessionId}")
    int softDelete(@Param("sessionId") String sessionId);
}
