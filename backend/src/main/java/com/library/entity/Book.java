package com.library.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
public class Book {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Long categoryId;
    private BigDecimal price;
    private Integer stock;
    private Integer available;
    private String coverUrl;
    private String description;
    private LocalDate publishDate;
    /**
     * 状态: 0-下架, 1-上架
     */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联字段（非数据库字段）
    private String categoryName;
}
