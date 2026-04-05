package com.example.onlinetest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Online Test API")
            .version("1.0")
            .description("API для управления квизами и вопросами")
            .contact(new Contact()
                .name("Developer")
                .email("dev@example.com")));
  }
}