package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Data
public class BorrowRecord {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    /**
     * 状态: 0-借阅中, 1-已归还, 2-已逾期
     */
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    
    // 关联字段（非数据库字段）
    private String username;
    private String bookTitle;
    private String bookIsbn;
}
