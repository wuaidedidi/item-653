package com.library.controller;

import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import com.library.util.PageResult;
import com.library.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借阅控制器
 */
@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
    
    @Autowired
    private BorrowService borrowService;
    
    /**
     * 分页查询借阅记录
     */
    @GetMapping
    public Result<PageResult<BorrowRecord>> getBorrowRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        
        Long currentUserId = (Long) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
        
        // 非管理员只能查看自己的记录
        if (!isAdmin) {
            userId = currentUserId;
        }
        
        PageResult<BorrowRecord> result = borrowService.getBorrowRecordsByPage(
                userId, bookId, status, keyword, page, size);
        return Result.success(result);
    }
    
    /**
     * 获取当前用户借阅记录
     */
    @GetMapping("/my")
    public Result<List<BorrowRecord>> getMyBorrowRecords(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<BorrowRecord> records = borrowService.getUserBorrowRecords(userId);
        return Result.success(records);
    }
    
    /**
     * 借书
     */
    @PostMapping("/borrow")
    public Result<BorrowRecord> borrowBook(
            @RequestParam Long bookId,
            @RequestParam(required = false) Integer days,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        BorrowRecord record = borrowService.borrowBook(userId, bookId, days);
        return Result.success("借书成功", record);
    }
    
    /**
     * 还书
     */
    @PostMapping("/return/{id}")
    public Result<BorrowRecord> returnBook(@PathVariable Long id) {
        BorrowRecord record = borrowService.returnBook(id);
        return Result.success("还书成功", record);
    }
    
    /**
     * 续借
     */
    @PostMapping("/renew/{id}")
    public Result<BorrowRecord> renewBook(
            @PathVariable Long id,
            @RequestParam(required = false) Integer days) {
        BorrowRecord record = borrowService.renewBook(id, days);
        return Result.success("续借成功", record);
    }
}
