package com.library.service;

import com.library.dto.BarrageRequest;
import com.library.entity.Barrage;
import com.library.mapper.BarrageMapper;
import com.library.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 弹幕服务
 */
@Service
public class BarrageService {
    
    private static final Logger logger = LoggerFactory.getLogger(BarrageService.class);
    
    private static final int DEFAULT_LIMIT = 100; // 默认获取弹幕数量
    
    @Autowired
    private BarrageMapper barrageMapper;
    
    /**
     * 获取图书弹幕
     */
    public List<Barrage> getBookBarrages(Long bookId, Integer limit) {
        return barrageMapper.findByBookId(bookId, limit != null ? limit : DEFAULT_LIMIT);
    }
    
    /**
     * 获取最新弹幕（弹幕墙）
     */
    public List<Barrage> getLatestBarrages(Integer limit) {
        return barrageMapper.findLatest(limit != null ? limit : DEFAULT_LIMIT);
    }
    
    /**
     * 分页查询弹幕
     */
    public PageResult<Barrage> getBarragesByPage(Long bookId, Long userId, Integer status, 
                                                  Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Barrage> barrages = barrageMapper.findByPage(bookId, userId, status, offset, size);
        Long total = barrageMapper.count(bookId, userId, status);
        
        return new PageResult<>(barrages, total, page, size);
    }
    
    /**
     * 发送弹幕
     */
    @Transactional
    public Barrage sendBarrage(Long userId, BarrageRequest request) {
        Barrage barrage = new Barrage();
        barrage.setUserId(userId);
        barrage.setBookId(request.getBookId());
        barrage.setContent(request.getContent());
        barrage.setColor(request.getColor() != null ? request.getColor() : "#FFFFFF");
        barrage.setPosition(request.getPosition() != null ? request.getPosition() : 0);
        barrage.setStatus(1); // 默认显示
        
        barrageMapper.insert(barrage);
        logger.info("弹幕发送成功: userId={}, content={}", userId, request.getContent());
        
        return barrageMapper.findById(barrage.getId());
    }
    
    /**
     * 切换弹幕状态
     */
    @Transactional
    public void toggleBarrageStatus(Long id) {
        Barrage barrage = barrageMapper.findById(id);
        if (barrage != null) {
            int newStatus = barrage.getStatus() == 1 ? 0 : 1;
            barrageMapper.updateStatus(id, newStatus);
            logger.info("弹幕状态更新: id={}, status={}", id, newStatus);
        }
    }
    
    /**
     * 删除弹幕
     */
    @Transactional
    public void deleteBarrage(Long id) {
        barrageMapper.deleteById(id);
        logger.info("弹幕删除成功: id={}", id);
    }
}
