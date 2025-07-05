package com.yu.fusionbase.web.user.custom.filter;

import com.yu.fusionbase.common.login.LoginUser;
import com.yu.fusionbase.common.login.LoginUserHolder;
import com.yu.fusionbase.common.utils.JwtUtil;
import com.yu.fusionbase.common.utils.LogUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 处理预检请求
        if (isPreflightRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 提取并验证Token
        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. 解析Token并设置安全上下文
            Claims claims = jwtUtil.parseToken(token);
            LoginUser loginUser = new LoginUser(
                    claims.get("userId", String.class),
                    claims.get("username", String.class)
            );

            setSecurityContext(loginUser);
            LoginUserHolder.setLoginUser(loginUser);

            // 4. 继续过滤器链
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleAuthenticationFailure(response, e);
        } finally {
            // 5. 清理线程局部变量
            LoginUserHolder.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private void setSecurityContext(LoginUser loginUser) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleAuthenticationFailure(HttpServletResponse response, Exception e)
            throws IOException {
        SecurityContextHolder.clearContext();
        LoginUserHolder.clear();
        LogUtil.error("JWT验证失败: {}", e);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的认证令牌");
    }
}