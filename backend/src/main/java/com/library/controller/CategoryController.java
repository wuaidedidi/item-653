package com.library.controller;

import com.library.entity.Category;
import com.library.service.CategoryService;
import com.library.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<Category>> getAllCategories(
            @RequestParam(required = false) String keyword) {
        List<Category> categories = categoryService.getAllCategories(keyword);
        return Result.success(categories);
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }
    
    /**
     * 创建分类（管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> createCategory(@Valid @RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return Result.success("创建成功", created);
    }
    
    /**
     * 更新分类（管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> updateCategory(@PathVariable Long id, 
                                           @Valid @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return Result.success("更新成功", updated);
    }
    
    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
