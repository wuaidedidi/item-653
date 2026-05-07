package com.library.mapper;

import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 借阅记录Mapper接口
 */
public interface BorrowRecordMapper {
    
    /**
     * 根据ID查询借阅记录
     */
    BorrowRecord findById(Long id);
    
    /**
     * 分页查询借阅记录
     */
    List<BorrowRecord> findByPage(@Param("userId") Long userId,
                                  @Param("bookId") Long bookId,
                                  @Param("status") Integer status,
                                  @Param("keyword") String keyword,
                                  @Param("offset") Integer offset,
                                  @Param("size") Integer size);
    
    /**
     * 查询借阅记录总数
     */
    Long count(@Param("userId") Long userId,
               @Param("bookId") Long bookId,
               @Param("status") Integer status,
               @Param("keyword") String keyword);
    
    /**
     * 查询用户当前借阅某书的记录
     */
    BorrowRecord findByUserAndBookBorrowing(@Param("userId") Long userId, 
                                            @Param("bookId") Long bookId);
    
    /**
     * 查询用户所有借阅记录
     */
    List<BorrowRecord> findByUserId(Long userId);
    
    /**
     * 插入借阅记录
     */
    int insert(BorrowRecord record);
    
    /**
     * 更新借阅记录
     */
    int update(BorrowRecord record);
    
    /**
     * 统计借阅中的数量
     */
    Long countBorrowing();
    
    /**
     * 统计逾期数量
     */
    Long countOverdue();
    
    /**
     * 统计今日借阅数量
     */
    Long countTodayBorrow();
    
    /**
     * 统计今日归还数量
     */
    Long countTodayReturn();
    
    /**
     * 检查图书是否有借阅中的记录
     */
    int countBorrowingByBookId(Long bookId);
}
