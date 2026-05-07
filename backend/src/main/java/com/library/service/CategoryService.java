package com.library.service;

import com.library.config.BusinessException;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务
 */
@Service
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    /**
     * 获取所有分类
     */
    public List<Category> getAllCategories(String keyword) {
        return categoryMapper.findAll(keyword);
    }
    
    /**
     * 根据ID获取分类
     */
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }
    
    /**
     * 创建分类
     */
    @Transactional
    public Category createCategory(Category category) {
        // 检查名称是否重复
        if (categoryMapper.findByName(category.getName()) != null) {
            throw new BusinessException("分类名称已存在");
        }
        
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        
        categoryMapper.insert(category);
        logger.info("分类创建成功: {}", category.getName());
        
        return category;
    }
    
    /**
     * 更新分类
     */
    @Transactional
    public Category updateCategory(Long id, Category request) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查名称是否重复（排除自己）
        Category existing = categoryMapper.findByName(request.getName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException("分类名称已存在");
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        
        categoryMapper.update(category);
        logger.info("分类更新成功: {}", category.getName());
        
        return category;
    }
    
    /**
     * 删除分类
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有关联图书
        int bookCount = categoryMapper.countBooks(id);
        if (bookCount > 0) {
            throw new BusinessException("该分类下有" + bookCount + "本图书，无法删除");
        }
        
        categoryMapper.deleteById(id);
        logger.info("分类删除成功: {}", category.getName());
    }
}
