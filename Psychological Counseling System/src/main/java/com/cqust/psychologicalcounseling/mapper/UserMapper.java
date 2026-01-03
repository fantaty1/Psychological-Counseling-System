package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

/**
 * 用户数据访问层
 * 负责用户表的CRUD操作
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE id = #{id} AND deleted_at IS NULL")
    Optional<User> findById(@Param("id") Long id);
    
    /**
     * 根据学号查询用户
     *
     * @param studentId 学号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE student_id = #{studentId} AND deleted_at IS NULL")
    Optional<User> findByStudentId(@Param("studentId") String studentId);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted_at IS NULL")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND deleted_at IS NULL")
    Optional<User> findByPhone(@Param("phone") String phone);
    
    /**
     * 根据学号或邮箱查询用户（用于登录）
     *
     * @param keyword 学号或邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE (student_id = #{keyword} OR email = #{keyword}) AND deleted_at IS NULL")
    Optional<User> findByStudentIdOrEmail(@Param("keyword") String keyword);
    
    /**
     * 检查学号是否已存在
     *
     * @param studentId 学号
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE student_id = #{studentId} AND deleted_at IS NULL")
    int countByStudentId(@Param("studentId") String studentId);
    
    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} AND deleted_at IS NULL")
    int countByEmail(@Param("email") String email);
    
    /**
     * 插入新用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    @Insert("INSERT INTO user (username, student_id, password, email, phone, avatar, college, major_class, " +
            "gender, status, checkin_streak, counseling_hours, appointments_count, allow_peer_view, " +
            "allow_notification, created_at, updated_at) " +
            "VALUES (#{username}, #{studentId}, #{password}, #{email}, #{phone}, #{avatar}, #{college}, " +
            "#{majorClass}, #{gender}, #{status}, #{checkinStreak}, #{counselingHours}, #{appointmentsCount}, " +
            "#{allowPeerView}, #{allowNotification}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE user SET updated_at = NOW()" +
            "<if test='username != null'>, username = #{username}</if>" +
            "<if test='email != null'>, email = #{email}</if>" +
            "<if test='phone != null'>, phone = #{phone}</if>" +
            "<if test='avatar != null'>, avatar = #{avatar}</if>" +
            "<if test='college != null'>, college = #{college}</if>" +
            "<if test='majorClass != null'>, major_class = #{majorClass}</if>" +
            "<if test='gender != null'>, gender = #{gender}</if>" +
            "<if test='allowPeerView != null'>, allow_peer_view = #{allowPeerView}</if>" +
            "<if test='allowNotification != null'>, allow_notification = #{allowNotification}</if>" +
            " WHERE id = #{id} AND deleted_at IS NULL" +
            "</script>")
    int update(User user);
    
    /**
     * 更新用户密码
     *
     * @param id       用户ID
     * @param password 新密码（已加密）
     * @return 影响行数
     */
    @Update("UPDATE user SET password = #{password}, updated_at = NOW() WHERE id = #{id} AND deleted_at IS NULL")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 更新用户签到连续天数
     *
     * @param id     用户ID
     * @param streak 连续天数
     * @return 影响行数
     */
    @Update("UPDATE user SET checkin_streak = #{streak}, updated_at = NOW() WHERE id = #{id}")
    int updateCheckinStreak(@Param("id") Long id, @Param("streak") Integer streak);
    
    /**
     * 增加用户预约次数
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @Update("UPDATE user SET appointments_count = appointments_count + 1, updated_at = NOW() WHERE id = #{id}")
    int incrementAppointmentsCount(@Param("id") Long id);
    
    /**
     * 增加用户咨询时长
     *
     * @param id    用户ID
     * @param hours 增加的小时数
     * @return 影响行数
     */
    @Update("UPDATE user SET counseling_hours = counseling_hours + #{hours}, updated_at = NOW() WHERE id = #{id}")
    int addCounselingHours(@Param("id") Long id, @Param("hours") java.math.BigDecimal hours);
    
    /**
     * 软删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @Update("UPDATE user SET deleted_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int softDelete(@Param("id") Long id);
    
    /**
     * 物理删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新用户头像
     *
     * @param id     用户ID
     * @param avatar 头像URL
     * @return 影响行数
     */
    @Update("UPDATE user SET avatar = #{avatar}, updated_at = NOW() WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
    
    /**
     * 查询所有用户（管理员用）
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM user WHERE deleted_at IS NULL ORDER BY created_at DESC")
    java.util.List<User> findAll();
    
    /**
     * 搜索用户（按用户名、学号、邮箱）
     *
     * @param keyword 关键词
     * @return 用户列表
     */
    @Select("SELECT * FROM user WHERE deleted_at IS NULL " +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR student_id LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY created_at DESC")
    java.util.List<User> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE user SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
