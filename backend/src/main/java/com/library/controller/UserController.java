package com.library.controller;

import com.library.dto.PasswordChangeRequest;
import com.library.dto.UserUpdateRequest;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.util.PageResult;
import com.library.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUserById(userId);
        return Result.success(user);
    }
    
    /**
     * 更新当前用户信息
     */
    @PutMapping("/me")
    public Result<User> updateCurrentUser(Authentication authentication, 
                                          @Valid @RequestBody UserUpdateRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        // 普通用户不能修改自己的角色和状态
        request.setRole(null);
        request.setStatus(null);
        User user = userService.updateUser(userId, request);
        return Result.success("更新成功", user);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/me/password")
    public Result<Void> changePassword(Authentication authentication,
                                       @Valid @RequestBody PasswordChangeRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 分页查询用户（管理员）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<User>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<User> result = userService.getUsersByPage(keyword, role, status, page, size);
        return Result.success(result);
    }
    
    /**
     * 获取用户详情（管理员）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }
    
    /**
     * 更新用户信息（管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> updateUser(@PathVariable Long id, 
                                   @Valid @RequestBody UserUpdateRequest request,
                                   Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        
        // 管理员不能修改自己的角色和状态
        if (id.equals(currentUserId)) {
            request.setRole(null);
            request.setStatus(null);
        }
        
        User user = userService.updateUser(id, request);
        return Result.success("更新成功", user);
    }
    
    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        
        // 管理员不能删除自己
        if (id.equals(currentUserId)) {
            return Result.badRequest("不能删除自己的账号");
        }
        
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }
}
