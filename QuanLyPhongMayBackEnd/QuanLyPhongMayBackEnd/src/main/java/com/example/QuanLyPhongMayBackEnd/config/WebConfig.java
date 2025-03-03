package com.example.QuanLyPhongMayBackEnd.config;

import com.example.QuanLyPhongMayBackEnd.filter.GlobalRateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebConfig {

    // Autowire the filter here
    @Autowired
    private GlobalRateLimitingFilter globalRateLimitingFilter;

    @Bean(name = "globalRateLimitingFilterBean")
    public FilterRegistrationBean<GlobalRateLimitingFilter> globalRateLimitingFilter() {
        FilterRegistrationBean<GlobalRateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(globalRateLimitingFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
