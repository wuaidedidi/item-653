package com.library.service;

import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘服务
 */
@Service
public class DashboardService {
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    /**
     * 获取仪表盘统计数据
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 图书统计
        stats.put("totalBooks", bookMapper.countTotal());
        
        // 用户统计
        stats.put("totalUsers", userMapper.count(null, null, null));
        
        // 借阅统计
        stats.put("borrowingCount", borrowRecordMapper.countBorrowing());
        stats.put("overdueCount", borrowRecordMapper.countOverdue());
        stats.put("todayBorrow", borrowRecordMapper.countTodayBorrow());
        stats.put("todayReturn", borrowRecordMapper.countTodayReturn());
        
        return stats;
    }
}
