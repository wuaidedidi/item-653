package com.library.config;

import com.library.entity.User;
import com.library.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 确保admin用户密码正确
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        int maxRetries = 5;
        for (int i = 1; i <= maxRetries; i++) {
            try {
                initData();
                return;
            } catch (Exception e) {
                logger.warn("数据初始化失败 (第{}次尝试): {}", i, e.getMessage());
                if (i < maxRetries) {
                    Thread.sleep(3000);
                } else {
                    logger.error("数据初始化在{}次重试后仍然失败，跳过初始化", maxRetries);
                }
            }
        }
    }

    private void initData() {
        // 检查并修复admin用户密码
        User admin = userMapper.findByUsername("admin");
        if (admin != null) {
            if (!passwordEncoder.matches("123456", admin.getPassword())) {
                logger.info("检测到admin用户密码不匹配，正在重新加密...");
                userMapper.updatePassword(admin.getId(), passwordEncoder.encode("123456"));
                logger.info("admin用户密码已修复");
            } else {
                logger.info("admin用户密码验证通过");
            }
        }

        // 检查并修复user用户密码
        User user = userMapper.findByUsername("user");
        if (user != null) {
            if (!passwordEncoder.matches("123456", user.getPassword())) {
                logger.info("检测到user用户密码不匹配，正在重新加密...");
                userMapper.updatePassword(user.getId(), passwordEncoder.encode("123456"));
                logger.info("user用户密码已修复");
            }
        }

        logger.info("数据初始化完成");
    }
}
