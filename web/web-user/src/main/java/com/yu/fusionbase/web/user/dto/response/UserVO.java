package com.yu.fusionbase.web.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户视图对象")
public class UserVO {

    @Schema(description = "用户ID", example = "123")
    private Long userId;

    @Schema(description = "用户名", example = "john_doe")
    private String username;

    @Schema(description = "邮箱", example = "john@example.com")
    private String email;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLogin;
}