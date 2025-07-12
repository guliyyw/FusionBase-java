package com.yu.fusionBase.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "用户信息表")
public class User extends BaseEntity {

    @Schema(description = "用户ID（随机uint）")
    @TableId(value = "user_id", type = IdType.INPUT)
    private String userId;

    @Schema(description = "用户名", example = "john_doe")
    @TableField(value = "username")
    private String username;

    @Schema(description = "加密密码", example = "$2a$10$...")
    @TableField(value = "password_hash")
    private String passwordHash;

    @Schema(description = "密码", example = "$2a$10$...")
    @TableField(value = "password")
    private String password;

    @Schema(description = "邮箱", example = "john@example.com")
    @TableField(value = "email")
    private String email;

    @Schema(description = "头像存储路径")
    @TableField(value = "avatar_path")
    private String avatarPath;

    @Schema(description = "最后登录时间")
    @TableField(value = "last_login")
    private LocalDateTime lastLogin;
}