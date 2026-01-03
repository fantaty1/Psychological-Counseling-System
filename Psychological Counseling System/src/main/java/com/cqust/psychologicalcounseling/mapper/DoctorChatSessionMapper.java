package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.DoctorChatSession;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 医生-学生聊天会话数据访问层
 */
@Mapper
public interface DoctorChatSessionMapper {
    
    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM doctor_chat_session WHERE session_id = #{sessionId}")
    @Results(id = "sessionResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "sessionId", column = "session_id"),
        @Result(property = "doctorId", column = "doctor_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "lastMessage", column = "last_message"),
        @Result(property = "doctorUnreadCount", column = "doctor_unread_count"),
        @Result(property = "studentUnreadCount", column = "student_unread_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    DoctorChatSession findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据咨询师ID查询所有会话
     */
    @Select("SELECT * FROM doctor_chat_session WHERE doctor_id = #{doctorId} ORDER BY updated_at DESC")
    @ResultMap("sessionResultMap")
    List<DoctorChatSession> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * 根据咨询师ID和学生ID查询会话
     */
    @Select("SELECT * FROM doctor_chat_session WHERE doctor_id = #{doctorId} AND student_id = #{studentId}")
    @ResultMap("sessionResultMap")
    DoctorChatSession findByDoctorIdAndStudentId(@Param("doctorId") Long doctorId, @Param("studentId") Long studentId);
    
    /**
     * 统计咨询师未读消息总数
     */
    @Select("SELECT COALESCE(SUM(doctor_unread_count), 0) FROM doctor_chat_session WHERE doctor_id = #{doctorId}")
    int countDoctorUnread(@Param("doctorId") Long doctorId);
    
    /**
     * 插入会话
     */
    @Insert("INSERT INTO doctor_chat_session (session_id, doctor_id, student_id, last_message, doctor_unread_count, student_unread_count, created_at, updated_at) " +
            "VALUES (#{sessionId}, #{doctorId}, #{studentId}, #{lastMessage}, #{doctorUnreadCount}, #{studentUnreadCount}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DoctorChatSession session);
    
    /**
     * 更新最后消息和未读数
     */
    @Update("UPDATE doctor_chat_session SET last_message = #{lastMessage}, doctor_unread_count = doctor_unread_count + #{doctorIncrement}, student_unread_count = student_unread_count + #{studentIncrement}, updated_at = NOW() WHERE session_id = #{sessionId}")
    int updateLastMessage(@Param("sessionId") String sessionId, 
                          @Param("lastMessage") String lastMessage, 
                          @Param("doctorIncrement") int doctorIncrement,
                          @Param("studentIncrement") int studentIncrement);
    
    /**
     * 清空咨询师未读数
     */
    @Update("UPDATE doctor_chat_session SET doctor_unread_count = 0, updated_at = NOW() WHERE session_id = #{sessionId}")
    int clearDoctorUnread(@Param("sessionId") String sessionId);
    
    /**
     * 清空学生未读数
     */
    @Update("UPDATE doctor_chat_session SET student_unread_count = 0, updated_at = NOW() WHERE session_id = #{sessionId}")
    int clearStudentUnread(@Param("sessionId") String sessionId);
    
    /**
     * 根据学生ID查询所有会话
     */
    @Select("SELECT * FROM doctor_chat_session WHERE student_id = #{studentId} ORDER BY updated_at DESC")
    @ResultMap("sessionResultMap")
    List<DoctorChatSession> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 统计学生未读消息总数
     */
    @Select("SELECT COALESCE(SUM(student_unread_count), 0) FROM doctor_chat_session WHERE student_id = #{studentId}")
    int countStudentUnread(@Param("studentId") Long studentId);
}
