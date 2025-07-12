package com.yu.fusionBase.web.user.custom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI().info(
                new Info()
                        .title("用户API")
                        .version("1.0")
                        .description("用户API"));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi albumApi() {
        return GroupedOpenApi.builder()
                .group("相册管理")
                .pathsToMatch("/album/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mediaApi() {
        return GroupedOpenApi.builder()
                .group("媒体管理")
                .pathsToMatch("/media/**")
                .build();
    }
}
