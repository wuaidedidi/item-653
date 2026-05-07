package com.library.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书请求DTO
 */
@Data
public class BookRequest {
    @NotBlank(message = "ISBN不能为空")
    @Size(max = 20, message = "ISBN长度不能超过20个字符")
    private String isbn;
    
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200个字符")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100个字符")
    private String author;
    
    @Size(max = 100, message = "出版社长度不能超过100个字符")
    private String publisher;
    
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal price;
    
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
    
    private String coverUrl;
    
    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;
    
    private LocalDate publishDate;
    
    private Integer status;
}
