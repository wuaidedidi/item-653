package com.library.service;

import com.library.config.BusinessException;
import com.library.dto.BookRequest;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 图书服务
 */
@Service
public class BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    /**
     * 分页查询图书
     */
    public PageResult<Book> getBooksByPage(String keyword, Long categoryId, Integer status, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Book> books = bookMapper.findByPage(keyword, categoryId, status, offset, size);
        Long total = bookMapper.count(keyword, categoryId, status);
        
        return new PageResult<>(books, total, page, size);
    }
    
    /**
     * 获取所有图书
     */
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }
    
    /**
     * 根据ID获取图书
     */
    public Book getBookById(Long id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return book;
    }
    
    /**
     * 创建图书
     */
    @Transactional
    public Book createBook(BookRequest request) {
        // 检查ISBN是否重复
        if (bookMapper.findByIsbn(request.getIsbn()) != null) {
            throw new BusinessException("ISBN已存在");
        }
        
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock() != null ? request.getStock() : 1);
        book.setAvailable(book.getStock()); // 初始可借数量等于库存
        book.setCoverUrl(request.getCoverUrl());
        book.setDescription(request.getDescription());
        book.setPublishDate(request.getPublishDate());
        book.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        
        bookMapper.insert(book);
        logger.info("图书创建成功: {}", book.getTitle());
        
        return bookMapper.findById(book.getId());
    }
    
    /**
     * 更新图书
     */
    @Transactional
    public Book updateBook(Long id, BookRequest request) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        // 检查ISBN是否重复（排除自己）
        Book existing = bookMapper.findByIsbn(request.getIsbn());
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException("ISBN已存在");
        }
        
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        
        // 计算可借数量变化
        int borrowedCount = book.getStock() - book.getAvailable();
        int newStock = request.getStock() != null ? request.getStock() : book.getStock();
        int newAvailable = newStock - borrowedCount;
        if (newAvailable < 0) {
            throw new BusinessException("库存不能小于已借出数量(" + borrowedCount + ")");
        }
        
        book.setStock(newStock);
        book.setAvailable(newAvailable);
        book.setCoverUrl(request.getCoverUrl());
        book.setDescription(request.getDescription());
        book.setPublishDate(request.getPublishDate());
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }
        
        bookMapper.update(book);
        logger.info("图书更新成功: {}", book.getTitle());
        
        return bookMapper.findById(id);
    }
    
    /**
     * 删除图书
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        // 检查是否有借阅中的记录
        int borrowingCount = borrowRecordMapper.countBorrowingByBookId(id);
        if (borrowingCount > 0) {
            throw new BusinessException("该图书有" + borrowingCount + "条借阅中的记录，无法删除");
        }
        
        bookMapper.deleteById(id);
        logger.info("图书删除成功: {}", book.getTitle());
    }
    
    /**
     * 获取图书总数
     */
    public Long getBookCount() {
        return bookMapper.countTotal();
    }
}
