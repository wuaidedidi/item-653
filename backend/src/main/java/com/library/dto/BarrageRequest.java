package com.library.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 弹幕请求DTO
 */
@Data
public class BarrageRequest {
    private Long bookId;
    
    @NotBlank(message = "弹幕内容不能为空")
    @Size(max = 100, message = "弹幕内容不能超过100个字符")
    private String content;
    
    private String color = "#FFFFFF";
    
    private Integer position = 0;
}
