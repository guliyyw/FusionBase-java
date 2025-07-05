package com.yu.fusionbase.common.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LoadConfigUtil {

    // 获取前端地址
    @Value("${app.frontend.url}")
    private String frontendUrl;

    // 获取后端地址
    @Value("${app.backend.url}")
    private String backendUrl;

    // 可选：获取完整的后端API地址
    public String getBackendApiUrl(String apiPath) {
        // 确保URL以/结尾，apiPath不以/开头
        if (!backendUrl.endsWith("/")) {
            backendUrl += "/";
        }
        if (apiPath.startsWith("/")) {
            apiPath = apiPath.substring(1);
        }
        return backendUrl + apiPath;
    }
}