package com.library.controller;

import com.library.dto.BarrageRequest;
import com.library.entity.Barrage;
import com.library.service.BarrageService;
import com.library.util.PageResult;
import com.library.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 弹幕控制器
 */
@RestController
@RequestMapping("/api/barrages")
public class BarrageController {
    
    @Autowired
    private BarrageService barrageService;
    
    /**
     * 获取图书弹幕（公开）
     */
    @GetMapping("/public/book/{bookId}")
    public Result<List<Barrage>> getBookBarrages(
            @PathVariable Long bookId,
            @RequestParam(required = false) Integer limit) {
        List<Barrage> barrages = barrageService.getBookBarrages(bookId, limit);
        return Result.success(barrages);
    }
    
    /**
     * 获取最新弹幕/弹幕墙（公开）
     */
    @GetMapping("/public/latest")
    public Result<List<Barrage>> getLatestBarrages(
            @RequestParam(required = false) Integer limit) {
        List<Barrage> barrages = barrageService.getLatestBarrages(limit);
        return Result.success(barrages);
    }
    
    /**
     * 分页查询弹幕（管理员）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Barrage>> getBarrages(
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Barrage> result = barrageService.getBarragesByPage(bookId, userId, status, page, size);
        return Result.success(result);
    }
    
    /**
     * 发送弹幕
     */
    @PostMapping
    public Result<Barrage> sendBarrage(@Valid @RequestBody BarrageRequest request,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Barrage barrage = barrageService.sendBarrage(userId, request);
        return Result.success("发送成功", barrage);
    }
    
    /**
     * 切换弹幕状态（管理员）
     */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> toggleBarrageStatus(@PathVariable Long id) {
        barrageService.toggleBarrageStatus(id);
        return Result.success("操作成功", null);
    }
    
    /**
     * 删除弹幕（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBarrage(@PathVariable Long id) {
        barrageService.deleteBarrage(id);
        return Result.success("删除成功", null);
    }
}
