package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.DoctorChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 医生-学生聊天消息数据访问层
 */
@Mapper
public interface DoctorChatMessageMapper {
    
    /**
     * 根据会话ID查询消息列表
     */
    @Select("SELECT * FROM doctor_chat_message WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    @Results(id = "messageResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "sessionId", column = "session_id"),
        @Result(property = "role", column = "role"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "content", column = "content"),
        @Result(property = "isRead", column = "is_read"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<DoctorChatMessage> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据会话ID分页查询消息
     */
    @Select("SELECT * FROM doctor_chat_message WHERE session_id = #{sessionId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    @ResultMap("messageResultMap")
    List<DoctorChatMessage> findBySessionIdWithPage(@Param("sessionId") String sessionId, 
                                                     @Param("offset") int offset, 
                                                     @Param("pageSize") int pageSize);
    
    /**
     * 统计会话消息数
     */
    @Select("SELECT COUNT(*) FROM doctor_chat_message WHERE session_id = #{sessionId}")
    int countBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询咨询师待回复的消息（学生发送的未读消息）
     */
    @Select("SELECT m.* FROM doctor_chat_message m " +
            "INNER JOIN doctor_chat_session s ON m.session_id = s.session_id " +
            "WHERE s.doctor_id = #{doctorId} AND m.role = 'student' AND m.is_read = 0 " +
            "ORDER BY m.created_at DESC")
    @ResultMap("messageResultMap")
    List<DoctorChatMessage> findPendingMessagesByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * 插入消息
     */
    @Insert("INSERT INTO doctor_chat_message (session_id, role, sender_id, content, is_read, created_at) " +
            "VALUES (#{sessionId}, #{role}, #{senderId}, #{content}, #{isRead}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DoctorChatMessage message);
    
    /**
     * 标记会话中某角色发送的消息为已读
     */
    @Update("UPDATE doctor_chat_message SET is_read = 1 WHERE session_id = #{sessionId} AND role = #{role}")
    int markAsRead(@Param("sessionId") String sessionId, @Param("role") String role);
    
    /**
     * 根据ID查询消息
     */
    @Select("SELECT * FROM doctor_chat_message WHERE id = #{id}")
    @ResultMap("messageResultMap")
    DoctorChatMessage findById(@Param("id") Long id);
}
