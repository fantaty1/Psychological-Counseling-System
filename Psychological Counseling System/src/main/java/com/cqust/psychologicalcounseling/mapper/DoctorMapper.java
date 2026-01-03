package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.Doctor;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

/**
 * 咨询师数据访问层
 * 负责咨询师表的CRUD操作
 */
@Mapper
public interface DoctorMapper {
    
    /**
     * 根据ID查询咨询师
     *
     * @param id 咨询师ID
     * @return 咨询师信息
     */
    @Select("SELECT * FROM doctor WHERE id = #{id} AND deleted_at IS NULL")
    Optional<Doctor> findById(@Param("id") Long id);
    
    /**
     * 根据手机号查询咨询师
     *
     * @param phone 手机号
     * @return 咨询师信息
     */
    @Select("SELECT * FROM doctor WHERE phone = #{phone} AND deleted_at IS NULL")
    Optional<Doctor> findByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查询咨询师
     *
     * @param email 邮箱
     * @return 咨询师信息
     */
    @Select("SELECT * FROM doctor WHERE email = #{email} AND deleted_at IS NULL")
    Optional<Doctor> findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号或邮箱查询咨询师（用于登录）
     *
     * @param keyword 手机号或邮箱
     * @return 咨询师信息
     */
    @Select("SELECT * FROM doctor WHERE (phone = #{keyword} OR email = #{keyword}) AND deleted_at IS NULL LIMIT 1")
    Optional<Doctor> findByPhoneOrEmail(@Param("keyword") String keyword);
    
    /**
     * 插入新咨询师
     *
     * @param doctor 咨询师信息
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO doctor (
            name, title, phone, password, email, avatar, description, 
            location,
            years, tags, methods, certifications, rating, total_hours, 
            helped_count, positive_rate, available, status, created_at, updated_at
        ) VALUES (
            #{name}, #{title}, #{phone}, #{password}, #{email}, #{avatar}, #{description},
            #{location},
            #{years}, #{tags}, #{methods}, #{certifications}, #{rating}, #{totalHours},
            #{helpedCount}, #{positiveRate}, #{available}, #{status}, #{createdAt}, #{updatedAt}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Doctor doctor);
    
    /**
     * 更新咨询师信息
     *
     * @param doctor 咨询师信息
     * @return 影响行数
     */
    @Update("""
        UPDATE doctor SET
            name = #{name},
            title = #{title},
            phone = #{phone},
            email = #{email},
            avatar = #{avatar},
            description = #{description},
            location = #{location},
            years = #{years},
            tags = #{tags},
            methods = #{methods},
            certifications = #{certifications},
            available = #{available},
            status = #{status},
            updated_at = #{updatedAt}
        WHERE id = #{id} AND deleted_at IS NULL
    """)
    int update(Doctor doctor);
    
    /**
     * 更新咨询师密码
     *
     * @param id 咨询师ID
     * @param password 新密码（已加密）
     * @return 影响行数
     */
    @Update("UPDATE doctor SET password = #{password}, updated_at = NOW() WHERE id = #{id} AND deleted_at IS NULL")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 更新咨询师评分统计
     *
     * @param id 咨询师ID
     * @param rating 评分
     * @param totalHours 总咨询时长
     * @param helpedCount 帮助人数
     * @param positiveRate 好评率
     * @return 影响行数
     */
    @Update("""
        UPDATE doctor SET
            rating = #{rating},
            total_hours = #{totalHours},
            helped_count = #{helpedCount},
            positive_rate = #{positiveRate},
            updated_at = NOW()
        WHERE id = #{id} AND deleted_at IS NULL
    """)
    int updateStats(@Param("id") Long id, 
                    @Param("rating") java.math.BigDecimal rating,
                    @Param("totalHours") java.math.BigDecimal totalHours,
                    @Param("helpedCount") Integer helpedCount,
                    @Param("positiveRate") Integer positiveRate);
    
    /**
     * 软删除咨询师
     *
     * @param id 咨询师ID
     * @return 影响行数
     */
    @Update("UPDATE doctor SET deleted_at = NOW() WHERE id = #{id} AND deleted_at IS NULL")
    int softDelete(@Param("id") Long id);
    
    /**
     * 物理删除咨询师
     *
     * @param id 咨询师ID
     * @return 影响行数
     */
    @Delete("DELETE FROM doctor WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询所有可用咨询师
     *
     * @return 咨询师列表
     */
    @Select("SELECT * FROM doctor WHERE status = 1 AND available = 1 AND deleted_at IS NULL ORDER BY rating DESC, helped_count DESC")
    java.util.List<Doctor> findAllAvailable();
    
    /**
     * 搜索咨询师（按姓名、职称、标签）
     *
     * @param keyword 关键词
     * @return 咨询师列表
     */
    @Select("SELECT * FROM doctor WHERE status = 1 AND deleted_at IS NULL " +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR tags LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY rating DESC, helped_count DESC")
    java.util.List<Doctor> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 更新咨询师头像
     *
     * @param id     咨询师ID
     * @param avatar 头像URL
     * @return 影响行数
     */
    @Update("UPDATE doctor SET avatar = #{avatar}, updated_at = NOW() WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
    
    /**
     * 查询所有咨询师（管理员用，包括未审核的）
     *
     * @return 咨询师列表
     */
    @Select("SELECT * FROM doctor WHERE deleted_at IS NULL ORDER BY created_at DESC")
    java.util.List<Doctor> findAll();
    
    /**
     * 更新咨询师状态
     *
     * @param id 咨询师ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE doctor SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
