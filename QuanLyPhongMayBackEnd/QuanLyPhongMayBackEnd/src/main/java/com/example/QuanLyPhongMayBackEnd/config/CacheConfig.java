package com.example.QuanLyPhongMayBackEnd.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching  // Bật caching trong Spring
public class CacheConfig {
    // Bạn có thể thêm các cấu hình cache khác tại đây nếu cần
}
