package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.Appointment;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约数据访问层
 */
@Mapper
public interface AppointmentMapper {
    
    /**
     * 根据ID查询预约
     */
    @Select("SELECT * FROM appointment WHERE id = #{id} AND deleted_at IS NULL")
    @Results(id = "appointmentResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "doctorId", column = "doctor_id"),
        @Result(property = "appointmentDate", column = "appointment_date"),
        @Result(property = "timeSlot", column = "time_slot"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "location", column = "location"),
        @Result(property = "rejectReason", column = "reject_reason"),
        @Result(property = "notes", column = "notes"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "deletedAt", column = "deleted_at")
    })
    Appointment findById(@Param("id") Long id);
    
    /**
     * 根据咨询师ID查询今日预约
     */
    @Select("SELECT * FROM appointment WHERE doctor_id = #{doctorId} AND appointment_date = #{date} AND deleted_at IS NULL ORDER BY time_slot")
    @ResultMap("appointmentResultMap")
    List<Appointment> findTodayByDoctorId(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    /**
     * 根据咨询师ID分页查询预约（带状态筛选）
     */
    @Select("<script>" +
            "SELECT * FROM appointment WHERE doctor_id = #{doctorId} AND deleted_at IS NULL " +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if> " +
            "ORDER BY appointment_date DESC, time_slot DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @ResultMap("appointmentResultMap")
    List<Appointment> findByDoctorIdWithPage(@Param("doctorId") Long doctorId, 
                                              @Param("status") String status,
                                              @Param("offset") int offset, 
                                              @Param("pageSize") int pageSize);
    
    /**
     * 统计咨询师预约总数（带状态筛选）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM appointment WHERE doctor_id = #{doctorId} AND deleted_at IS NULL " +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            "</script>")
    int countByDoctorId(@Param("doctorId") Long doctorId, @Param("status") String status);
    
    /**
     * 统计咨询师今日各状态预约数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE doctor_id = #{doctorId} AND appointment_date = #{date} AND deleted_at IS NULL")
    int countTodayByDoctorId(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    /**
     * 统计咨询师今日已完成预约数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE doctor_id = #{doctorId} AND appointment_date = #{date} AND status = 'completed' AND deleted_at IS NULL")
    int countTodayCompletedByDoctorId(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    /**
     * 统计咨询师今日待处理预约数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE doctor_id = #{doctorId} AND appointment_date = #{date} AND status = 'pending' AND deleted_at IS NULL")
    int countTodayPendingByDoctorId(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    /**
     * 统计咨询师所有待处理预约数（不限日期）
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE doctor_id = #{doctorId} AND status = 'pending' AND deleted_at IS NULL")
    int countAllPendingByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * 根据学生ID查询预约
     */
    @Select("SELECT * FROM appointment WHERE student_id = #{studentId} AND deleted_at IS NULL ORDER BY appointment_date DESC, time_slot DESC")
    @ResultMap("appointmentResultMap")
    List<Appointment> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 插入预约
     */
    @Insert("INSERT INTO appointment (student_id, doctor_id, appointment_date, time_slot, type, status, description, location, created_at, updated_at) " +
            "VALUES (#{studentId}, #{doctorId}, #{appointmentDate}, #{timeSlot}, #{type}, #{status}, #{description}, #{location}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Appointment appointment);
    
    /**
     * 更新预约状态
     */
    @Update("UPDATE appointment SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 确认预约
     */
    @Update("UPDATE appointment SET status = 'confirmed', location = #{location}, updated_at = NOW() WHERE id = #{id}")
    int confirm(@Param("id") Long id, @Param("location") String location);
    
    /**
     * 拒绝预约
     */
    @Update("UPDATE appointment SET status = 'rejected', reject_reason = #{reason}, updated_at = NOW() WHERE id = #{id}")
    int reject(@Param("id") Long id, @Param("reason") String reason);
    
    /**
     * 完成预约
     */
    @Update("UPDATE appointment SET status = 'completed', notes = #{notes}, updated_at = NOW() WHERE id = #{id}")
    int complete(@Param("id") Long id, @Param("notes") String notes);
    
    /**
     * 软删除预约
     */
    @Update("UPDATE appointment SET deleted_at = NOW() WHERE id = #{id}")
    int softDelete(@Param("id") Long id);
    
    /**
     * 检查时间段是否已被预约
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE doctor_id = #{doctorId} AND appointment_date = #{date} AND time_slot = #{timeSlot} AND status IN ('pending', 'confirmed') AND deleted_at IS NULL")
    int checkTimeSlotBooked(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("timeSlot") String timeSlot);
}
