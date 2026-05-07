package com.library.controller;

import com.library.dto.BookRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import com.library.util.PageResult;
import com.library.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    /**
     * 分页查询图书
     */
    @GetMapping
    public Result<PageResult<Book>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Book> result = bookService.getBooksByPage(keyword, categoryId, status, page, size);
        return Result.success(result);
    }
    
    /**
     * 获取所有图书
     */
    @GetMapping("/all")
    public Result<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return Result.success(books);
    }
    
    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return Result.success(book);
    }
    
    /**
     * 创建图书（管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Book> createBook(@Valid @RequestBody BookRequest request) {
        Book book = bookService.createBook(request);
        return Result.success("创建成功", book);
    }
    
    /**
     * 更新图书（管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Book> updateBook(@PathVariable Long id, 
                                   @Valid @RequestBody BookRequest request) {
        Book book = bookService.updateBook(id, request);
        return Result.success("更新成功", book);
    }
    
    /**
     * 删除图书（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.success("删除成功", null);
    }
}
