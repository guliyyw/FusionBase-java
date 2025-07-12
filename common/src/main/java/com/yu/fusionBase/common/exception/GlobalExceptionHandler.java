package com.yu.fusionBase.common.exception;

import com.yu.fusionBase.common.result.Result;
import com.yu.fusionBase.common.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@ControllerAdvice
//全局异常处理
public class GlobalExceptionHandler {

    //统一处理控制器异常的注解
    @ExceptionHandler(Exception.class)
    //将返回值写入HTTP响应体
    @ResponseBody
    public Result handle(Exception e) {
        logException(e, "系统异常");
        return Result.fail();
    }

    @ExceptionHandler(FusionBaseException.class)
    @ResponseBody
    public Result handle(FusionBaseException e) {
        logException(e,"业务异常");
        return Result.fail(e.getCode(),e.getMessage());
    }

    private void logException(Throwable e, String errorType) {
        try {
            // 获取当前请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 构建详细的错误信息
                String errorMsg = String.format(
                        "%s [请求路径: %s %s] [客户端IP: %s] [错误信息: %s]",
                        errorType,
                        request.getMethod(),
                        request.getRequestURI(),
                        getClientIp(request),
                        e.getMessage()
                );

                // 使用日志工具记录
                LogUtil.error(errorMsg, e);
            } else {
                // 非Web请求场景
                LogUtil.error(errorType + ": " + e.getMessage(), e);
            }
        } catch (Exception logEx) {
            // 日志记录失败时的备选方案
            System.err.println("记录日志失败: " + logEx.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
