package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图书分类实体类
 */
@Data
public class Category {
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
