package com.yu.fusionbase.web.user.dto.response;

import com.yu.fusionbase.model.enums.PermissionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "相册视图对象")
public class AlbumVO {
    
    @Schema(description = "相册ID", example = "123")
    private Long albumId;
    
    @Schema(description = "相册名称", example = "我的旅行相册")
    private String name;
    
    @Schema(description = "相册描述", example = "2023年旅行记录")
    private String description;
    
    @Schema(description = "封面图URL")
    private String coverUrl;
    
    @Schema(description = "媒体数量", example = "15")
    private Integer mediaCount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
    
    @Schema(description = "是否公开", example = "false")
    private Boolean isPublic;
    
    @Schema(description = "当前用户权限", example = "OWNER")
    private PermissionLevel permission;
}