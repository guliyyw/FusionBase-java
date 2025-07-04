package com.yu.fusionbase.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yu.fusionbase.model.enums.PermissionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("album_share")
@Schema(description = "相册共享关系表")
public class AlbumShare extends BaseEntity {

    @Schema(description = "被共享的相册ID")
    @TableField(value = "album_id")
    private Long albumId;

    @Schema(description = "相册所有者ID")
    @TableField(value = "owner_id")
    private Long ownerId;

    @Schema(description = "被共享的用户ID")
    @TableField(value = "shared_user_id")
    private Long sharedUserId;

    @Schema(description = "权限级别")
    @TableField(value = "permission_level")
    private PermissionLevel permissionLevel;

    @Schema(description = "共享时间")
    @TableField(value = "shared_at")
    private LocalDateTime sharedAt;

    @Schema(description = "共享过期时间")
    @TableField(value = "expires_at")
    private LocalDateTime expiresAt;
}