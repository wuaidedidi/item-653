package com.library.mapper;

import com.library.entity.Category;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 分类Mapper接口
 */
public interface CategoryMapper {
    
    /**
     * 根据ID查询分类
     */
    Category findById(Long id);
    
    /**
     * 根据名称查询分类
     */
    Category findByName(String name);
    
    /**
     * 查询所有分类
     */
    List<Category> findAll(@Param("keyword") String keyword);
    
    /**
     * 插入分类
     */
    int insert(Category category);
    
    /**
     * 更新分类
     */
    int update(Category category);
    
    /**
     * 删除分类
     */
    int deleteById(Long id);
    
    /**
     * 检查分类是否有关联图书
     */
    int countBooks(Long categoryId);
}
