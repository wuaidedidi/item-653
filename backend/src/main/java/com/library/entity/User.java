package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    /**
     * 角色: 0-普通用户, 1-管理员
     */
    private Integer role;
    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
