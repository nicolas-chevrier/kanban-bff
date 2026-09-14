package com.kanban.kanbanbff.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workOrderBffOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Work Order BFF API")
                        .description("BFF Spring Boot exposant les ordres de travail pour react-kanban")
                        .version("v1"));
    }
}
