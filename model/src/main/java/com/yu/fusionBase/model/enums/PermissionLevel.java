package com.yu.fusionBase.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "权限级别枚举")
public enum PermissionLevel {
    @Schema(description = "查看") VIEWER,
    @Schema(description = "修改") CONTRIBUTOR,
    @Schema(description = "管理") MANAGER
}