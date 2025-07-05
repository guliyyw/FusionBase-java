package com.yu.fusionbase.web.user.dto.request;

import com.yu.fusionbase.model.enums.PermissionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Schema(description = "相册共享请求DTO")
public class AlbumShareDTO {

    @Schema(description = "被共享用户ID", example = "123")
    private Long sharedUserId;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "被邀请人邮箱(用于邀请未注册用户)", example = "user@example.com")
    private String inviteeEmail;

    @NotNull(message = "权限级别不能为空")
    @Schema(description = "权限级别", example = "VIEWER")
    private PermissionLevel permissionLevel;

    @Schema(description = "共享过期时间")
    private LocalDateTime expiresAt;
}