package com.yu.fusionBase.web.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "相册创建请求")
public class AlbumCreateDTO {

    @NotBlank(message = "相册名称不能为空")
    @Size(max = 100, message = "相册名称长度不能超过100个字符")
    @Schema(description = "相册名称", example = "我的旅行相册", required = true)
    private String name;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Schema(description = "相册描述", example = "2023年旅行记录")
    private String description;

    @Schema(description = "是否公开相册", example = "false")
    private Boolean isPublic = false;
}