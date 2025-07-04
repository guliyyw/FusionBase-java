package com.yu.fusionbase.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "邀请状态枚举")
public enum InvitationStatus {
    @Schema(description = "待处理") PENDING,
    @Schema(description = "已接受") ACCEPTED,
    @Schema(description = "已过期") EXPIRED,
    @Schema(description = "已撤销") REVOKED
}