package com.yu.fusionbase.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.function.Supplier;

/**
 * 日志工具类 - 基于 SLF4J + Logback
 */
public final class LogUtil {

    private LogUtil() {
        // 私有构造防止实例化
    }

    /**
     * 获取当前调用类的Logger
     */
    private static Logger getLogger() {
        // 获取调用此方法的类名
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callerClassName = stackTrace[3].getClassName();
        return LoggerFactory.getLogger(callerClassName);
    }

    public static void trace(String msg) {
        getLogger().trace(msg);
    }

    public static void debug(String msg) {
        getLogger().debug(msg);
    }

    public static void info(String msg) {
        getLogger().info(msg);
    }

    public static void warn(String msg) {
        getLogger().warn(msg);
    }

    public static void error(String msg) {
        getLogger().error(msg);
    }

    public static void error(String msg, Throwable t) {
        getLogger().error(msg, t);
    }

    public static void error(String msg, Object... args) {
        getLogger().error(msg, args);
    }

    // 带参数的日志方法（避免字符串拼接开销）
    public static void info(String format, Object... args) {
        getLogger().info(format, args);
    }

    public static void debug(String format, Object... args) {
        getLogger().debug(format, args);
    }

    // 延迟计算日志内容（用于高性能场景）
    public static void debug(Supplier<String> messageSupplier) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(messageSupplier.get());
        }
    }

    // MDC (Mapped Diagnostic Context) 操作
    public static void putMdc(String key, String value) {
        MDC.put(key, value);
    }

    public static String getMdc(String key) {
        return MDC.get(key);
    }

    public static void removeMdc(String key) {
        MDC.remove(key);
    }

    public static void clearMdc() {
        MDC.clear();
    }

    // 业务日志快捷方法
    public static void logBusinessEvent(String eventType, String eventData) {
        putMdc("eventType", eventType);
        getLogger().info("[业务事件] {}", eventData);
        removeMdc("eventType");
    }

    // 性能日志快捷方法
    public static void logPerf(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        getLogger().info("[性能监控] {} 耗时: {}ms", operation, duration);
    }
}