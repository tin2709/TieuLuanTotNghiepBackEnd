package com.example.QuanLyPhongMayBackEnd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi publicApi(@Value("${openapi.service.api-docs}") String apiDocs) {
        // Cấu hình nhóm API để Swagger có thể nhóm tài liệu API
        return GroupedOpenApi.builder()
                .group(apiDocs)  // Ví dụ: /v3/api-docs/api-service
                .packagesToScan("com.example.QuanLyPhongMayBackEnd.controller")  // Chỉ quét controller trong package này
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("${openapi.service.title}") String title,
            @Value("${openapi.service.version}") String version,
            @Value("${openapi.service.server}") String serverUrl) {
        // Cấu hình thông tin cho Swagger API
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl)))  // URL của server nơi API đang chạy
                .info(new Info().title(title)
                        .description("APIdocuments")  // Mô tả API
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));  // Thêm License cho API
    }
}
