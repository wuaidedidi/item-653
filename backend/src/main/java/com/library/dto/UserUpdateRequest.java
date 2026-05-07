package com.library.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateRequest {
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String avatar;
    
    private Integer role;
    
    private Integer status;
}
