package com.library.controller;

import com.library.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务运行正常", "OK");
    }
}
