package com.library.service;

import com.library.config.BusinessException;
import com.library.dto.LoginRequest;
import com.library.dto.PasswordChangeRequest;
import com.library.dto.RegisterRequest;
import com.library.dto.UserUpdateRequest;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.util.JwtUtil;
import com.library.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", sanitizeUser(user));
        
        logger.info("用户登录成功: {}", user.getUsername());
        return result;
    }
    
    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 检查用户名是否存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(0); // 默认普通用户
        user.setStatus(1); // 默认启用
        
        userMapper.insert(user);
        logger.info("用户注册成功: {}", user.getUsername());
        
        return sanitizeUser(user);
    }
    
    /**
     * 获取用户信息
     */
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return sanitizeUser(user);
    }
    
    /**
     * 分页查询用户
     */
    public PageResult<User> getUsersByPage(String keyword, Integer role, Integer status, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<User> users = userMapper.findByPage(keyword, role, status, offset, size);
        Long total = userMapper.count(keyword, role, status);
        
        // 清除密码
        users.forEach(u -> u.setPassword(null));
        
        return new PageResult<>(users, total, page, size);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        userMapper.update(user);
        logger.info("用户信息更新成功: {}", user.getUsername());
        
        return sanitizeUser(user);
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        userMapper.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
        logger.info("用户密码修改成功: {}", user.getUsername());
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        userMapper.deleteById(id);
        logger.info("用户删除成功: {}", user.getUsername());
    }
    
    /**
     * 清除用户敏感信息
     */
    private User sanitizeUser(User user) {
        user.setPassword(null);
        return user;
    }
}
