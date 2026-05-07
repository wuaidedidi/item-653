package com.library.mapper;

import com.library.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户Mapper接口
 */
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    User findById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 查询所有用户
     */
    List<User> findAll(@Param("keyword") String keyword, 
                       @Param("role") Integer role,
                       @Param("status") Integer status);
    
    /**
     * 查询用户总数
     */
    Long count(@Param("keyword") String keyword, 
               @Param("role") Integer role,
               @Param("status") Integer status);
    
    /**
     * 分页查询用户
     */
    List<User> findByPage(@Param("keyword") String keyword,
                          @Param("role") Integer role,
                          @Param("status") Integer status,
                          @Param("offset") Integer offset,
                          @Param("size") Integer size);
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 更新用户
     */
    int update(User user);
    
    /**
     * 更新密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 删除用户
     */
    int deleteById(Long id);
}
