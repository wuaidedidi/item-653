package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 图书Mapper接口
 */
public interface BookMapper {
    
    /**
     * 根据ID查询图书
     */
    Book findById(Long id);
    
    /**
     * 根据ISBN查询图书
     */
    Book findByIsbn(String isbn);
    
    /**
     * 分页查询图书
     */
    List<Book> findByPage(@Param("keyword") String keyword,
                          @Param("categoryId") Long categoryId,
                          @Param("status") Integer status,
                          @Param("offset") Integer offset,
                          @Param("size") Integer size);
    
    /**
     * 查询图书总数
     */
    Long count(@Param("keyword") String keyword,
               @Param("categoryId") Long categoryId,
               @Param("status") Integer status);
    
    /**
     * 查询所有图书
     */
    List<Book> findAll();
    
    /**
     * 插入图书
     */
    int insert(Book book);
    
    /**
     * 更新图书
     */
    int update(Book book);
    
    /**
     * 更新库存
     */
    int updateStock(@Param("id") Long id, 
                    @Param("stock") Integer stock, 
                    @Param("available") Integer available);
    
    /**
     * 删除图书
     */
    int deleteById(Long id);
    
    /**
     * 获取图书总数（统计用）
     */
    Long countTotal();
}
