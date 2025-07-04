package com.yu.fusionbase.web.user.custom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF
            .authorizeHttpRequests(authorize -> authorize
                // 放行Knife4j/Swagger资源
                .requestMatchers(
                    "/doc.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/favicon.ico",
                    "/user/login",
                    "/user/register"
                ).permitAll()
                
                // 放行登录相关端点
                .requestMatchers("/login/**").permitAll()
                
                // 管理员路径需要认证（与您的拦截器一致）
                .requestMatchers("/user/**").authenticated()
                
                // 其他请求默认放行
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login") // 自定义登录页
                .permitAll()
            );

        return http.build();
    }
}