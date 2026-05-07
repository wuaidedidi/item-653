package com.library.service;

import com.library.config.BusinessException;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅服务
 */
@Service
public class BorrowService {
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);
    
    private static final int DEFAULT_BORROW_DAYS = 30; // 默认借阅天数
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    /**
     * 分页查询借阅记录
     */
    public PageResult<BorrowRecord> getBorrowRecordsByPage(Long userId, Long bookId, Integer status, 
                                                           String keyword, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<BorrowRecord> records = borrowRecordMapper.findByPage(userId, bookId, status, keyword, offset, size);
        Long total = borrowRecordMapper.count(userId, bookId, status, keyword);
        
        // 检查逾期状态
        LocalDateTime now = LocalDateTime.now();
        for (BorrowRecord record : records) {
            if (record.getStatus() == 0 && record.getDueDate().isBefore(now)) {
                record.setStatus(2); // 标记为逾期
            }
        }
        
        return new PageResult<>(records, total, page, size);
    }
    
    /**
     * 获取用户借阅记录
     */
    public List<BorrowRecord> getUserBorrowRecords(Long userId) {
        return borrowRecordMapper.findByUserId(userId);
    }
    
    /**
     * 借书
     */
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId, Integer days) {
        // 检查图书是否存在
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        // 检查图书状态
        if (book.getStatus() != 1) {
            throw new BusinessException("该图书已下架");
        }
        
        // 检查库存
        if (book.getAvailable() <= 0) {
            throw new BusinessException("该图书暂无库存");
        }
        
        // 检查是否重复借阅
        BorrowRecord existing = borrowRecordMapper.findByUserAndBookBorrowing(userId, bookId);
        if (existing != null) {
            throw new BusinessException("您已借阅此书，请先归还后再借");
        }
        
        // 创建借阅记录
        int borrowDays = days != null && days > 0 ? days : DEFAULT_BORROW_DAYS;
        LocalDateTime now = LocalDateTime.now();
        
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(now);
        record.setDueDate(now.plusDays(borrowDays));
        record.setStatus(0); // 借阅中
        
        borrowRecordMapper.insert(record);
        
        // 减少可借数量
        bookMapper.updateStock(bookId, book.getStock(), book.getAvailable() - 1);
        
        logger.info("借书成功: userId={}, bookId={}, recordId={}", userId, bookId, record.getId());
        
        return borrowRecordMapper.findById(record.getId());
    }
    
    /**
     * 还书
     */
    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (record.getStatus() == 1) {
            throw new BusinessException("该记录已归还");
        }
        
        // 更新记录
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(1); // 已归还
        borrowRecordMapper.update(record);
        
        // 增加可借数量
        Book book = bookMapper.findById(record.getBookId());
        if (book != null) {
            bookMapper.updateStock(book.getId(), book.getStock(), book.getAvailable() + 1);
        }
        
        logger.info("还书成功: recordId={}", recordId);
        
        return borrowRecordMapper.findById(recordId);
    }
    
    /**
     * 续借
     */
    @Transactional
    public BorrowRecord renewBook(Long recordId, Integer days) {
        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (record.getStatus() != 0) {
            throw new BusinessException("只有借阅中的记录可以续借");
        }
        
        int renewDays = days != null && days > 0 ? days : DEFAULT_BORROW_DAYS;
        record.setDueDate(record.getDueDate().plusDays(renewDays));
        record.setRemark("已续借" + renewDays + "天");
        borrowRecordMapper.update(record);
        
        logger.info("续借成功: recordId={}, days={}", recordId, renewDays);
        
        return borrowRecordMapper.findById(recordId);
    }
}
