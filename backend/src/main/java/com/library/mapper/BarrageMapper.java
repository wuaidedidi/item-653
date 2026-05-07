package com.library.mapper;

import com.library.entity.Barrage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 弹幕Mapper接口
 */
public interface BarrageMapper {
    
    /**
     * 根据ID查询弹幕
     */
    Barrage findById(Long id);
    
    /**
     * 查询图书相关弹幕
     */
    List<Barrage> findByBookId(@Param("bookId") Long bookId, 
                               @Param("limit") Integer limit);
    
    /**
     * 查询最新弹幕（弹幕墙）
     */
    List<Barrage> findLatest(@Param("limit") Integer limit);
    
    /**
     * 分页查询弹幕
     */
    List<Barrage> findByPage(@Param("bookId") Long bookId,
                             @Param("userId") Long userId,
                             @Param("status") Integer status,
                             @Param("offset") Integer offset,
                             @Param("size") Integer size);
    
    /**
     * 查询弹幕总数
     */
    Long count(@Param("bookId") Long bookId,
               @Param("userId") Long userId,
               @Param("status") Integer status);
    
    /**
     * 插入弹幕
     */
    int insert(Barrage barrage);
    
    /**
     * 更新弹幕状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除弹幕
     */
    int deleteById(Long id);
}
