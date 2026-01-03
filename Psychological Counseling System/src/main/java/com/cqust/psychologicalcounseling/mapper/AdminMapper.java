package com.cqust.psychologicalcounseling.mapper;

import com.cqust.psychologicalcounseling.entity.Admin;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 管理员数据访问层
 * 负责管理员表的CRUD操作
 */
@Mapper
public interface AdminMapper {
    
    /**
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Optional<Admin> findById(@Param("id") Long id);
    
    /**
     * 根据用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员信息
     */
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Optional<Admin> findByUsername(@Param("username") String username);
    
    /**
     * 插入新管理员
     *
     * @param admin 管理员信息
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO admin (
            username, password, name, role, status, created_at, updated_at
        ) VALUES (
            #{username}, #{password}, #{name}, #{role}, #{status}, #{createdAt}, #{updatedAt}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);
    
    /**
     * 更新最后登录时间
     *
     * @param id 管理员ID
     * @param lastLoginAt 最后登录时间
     * @return 影响行数
     */
    @Update("UPDATE admin SET last_login_at = #{lastLoginAt} WHERE id = #{id}")
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);
}
