package com.yu.fusionbase.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文件类型枚举")
public enum FileType {
    @Schema(description = "图片") IMAGE,
    @Schema(description = "视频") VIDEO
}