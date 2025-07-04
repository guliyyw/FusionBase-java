package com.yu.fusionbase.web.user.custom.interceptor;

import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.login.LoginUser;
import com.yu.fusionbase.common.login.LoginUserHolder;
import com.yu.fusionbase.common.result.ResultCodeEnum;
import com.yu.fusionbase.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//拦截器
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    //校验登录token
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("access-token");

        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        //保存信息到进程
        LoginUserHolder.setLoginUser(new LoginUser(userId,username));


        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
