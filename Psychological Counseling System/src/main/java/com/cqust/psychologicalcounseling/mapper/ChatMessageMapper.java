package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI聊天消息数据访问层
 */
@Mapper
public interface ChatMessageMapper {
    
    /**
     * 根据会话ID查询消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<ChatMessage> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 插入新消息
     *
     * @param message 消息信息
     * @return 影响行数
     */
    @Insert("INSERT INTO chat_message (session_id, role, content, type, risk_level, created_at) " +
            "VALUES (#{sessionId}, #{role}, #{content}, #{type}, #{riskLevel}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);
    
    /**
     * 批量插入消息
     *
     * @param messages 消息列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO chat_message (session_id, role, content, type, risk_level, created_at) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.sessionId}, #{item.role}, #{item.content}, #{item.type}, #{item.riskLevel}, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<ChatMessage> messages);
    
    /**
     * 根据会话ID删除消息
     *
     * @param sessionId 会话ID
     * @return 影响行数
     */
    @Delete("DELETE FROM chat_message WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
