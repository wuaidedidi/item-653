package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 弹幕实体类
 */
@Data
public class Barrage {
    private Long id;
    private Long userId;
    private Long bookId;
    private String content;
    /**
     * 弹幕颜色 (十六进制)
     */
    private String color;
    /**
     * 弹幕位置: 0-滚动, 1-顶部, 2-底部
     */
    private Integer position;
    /**
     * 状态: 0-隐藏, 1-显示
     */
    private Integer status;
    private LocalDateTime createdAt;
    
    // 关联字段（非数据库字段）
    private String username;
    private String nickname;
    private String bookTitle;
}
