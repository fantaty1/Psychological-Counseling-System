package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.DoctorSchedule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 咨询师排班数据访问层
 */
@Mapper
public interface DoctorScheduleMapper {
    
    /**
     * 根据咨询师ID和日期范围查询排班
     */
    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND schedule_date >= #{startDate} AND schedule_date <= #{endDate} ORDER BY schedule_date, time_slot")
    @Results(id = "scheduleResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "doctorId", column = "doctor_id"),
        @Result(property = "scheduleDate", column = "schedule_date"),
        @Result(property = "timeSlot", column = "time_slot"),
        @Result(property = "available", column = "available"),
        @Result(property = "booked", column = "booked"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<DoctorSchedule> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId, 
                                                      @Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
    
    /**
     * 根据咨询师ID和具体日期查询排班
     */
    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND schedule_date = #{date} ORDER BY time_slot")
    @ResultMap("scheduleResultMap")
    List<DoctorSchedule> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    /**
     * 查询特定时间段排班
     */
    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND schedule_date = #{date} AND time_slot = #{timeSlot}")
    @ResultMap("scheduleResultMap")
    DoctorSchedule findByDoctorIdDateAndSlot(@Param("doctorId") Long doctorId, 
                                              @Param("date") LocalDate date, 
                                              @Param("timeSlot") String timeSlot);
    
    /**
     * 插入排班
     */
    @Insert("INSERT INTO doctor_schedule (doctor_id, schedule_date, time_slot, available, booked, created_at, updated_at) " +
            "VALUES (#{doctorId}, #{scheduleDate}, #{timeSlot}, #{available}, #{booked}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DoctorSchedule schedule);
    
    /**
     * 更新排班可用状态
     */
    @Update("UPDATE doctor_schedule SET available = #{available}, updated_at = NOW() WHERE id = #{id}")
    int updateAvailable(@Param("id") Long id, @Param("available") Integer available);
    
    /**
     * 更新排班预约状态
     */
    @Update("UPDATE doctor_schedule SET booked = #{booked}, updated_at = NOW() WHERE doctor_id = #{doctorId} AND schedule_date = #{date} AND time_slot = #{timeSlot}")
    int updateBooked(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("timeSlot") String timeSlot, @Param("booked") Integer booked);
    
    /**
     * 批量删除咨询师某日期范围的排班
     */
    @Delete("DELETE FROM doctor_schedule WHERE doctor_id = #{doctorId} AND schedule_date >= #{startDate} AND schedule_date <= #{endDate}")
    int deleteByDoctorIdAndDateRange(@Param("doctorId") Long doctorId, 
                                      @Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    /**
     * 批量插入排班
     */
    @Insert("<script>" +
            "INSERT INTO doctor_schedule (doctor_id, schedule_date, time_slot, available, booked, created_at, updated_at) VALUES " +
            "<foreach collection='schedules' item='s' separator=','>" +
            "(#{s.doctorId}, #{s.scheduleDate}, #{s.timeSlot}, #{s.available}, #{s.booked}, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("schedules") List<DoctorSchedule> schedules);
    
    /**
     * 检查时间段是否可预约
     */
    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND schedule_date = #{date} AND time_slot = #{timeSlot} AND available = 1 AND booked = 0")
    @ResultMap("scheduleResultMap")
    DoctorSchedule checkSlotAvailable(@Param("doctorId") Long doctorId, 
                                       @Param("date") LocalDate date, 
                                       @Param("timeSlot") String timeSlot);
}
