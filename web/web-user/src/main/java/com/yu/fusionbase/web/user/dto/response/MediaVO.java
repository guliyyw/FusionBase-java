package com.yu.fusionbase.web.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "媒体视图对象")
public class MediaVO {

    @Schema(description = "媒体ID", example = "media_67890")
    private String mediaId;

    @Schema(description = "所属相册ID", example = "album_12345")
    private String albumId;

    @Schema(description = "上传用户ID", example = "user_001")
    private String userId;

    @Schema(description = "原始文件名", example = "sunset.jpg")
    private String fileName;

    @Schema(description = "文件类型（image/video）", example = "image")
    private String fileType;

    @Schema(description = "文件大小（字节）", example = "204800")
    private Long fileSize;

    @Schema(description = "媒体宽度（像素）", example = "1920")
    private Integer width;

    @Schema(description = "媒体高度（像素）", example = "1080")
    private Integer height;

    @Schema(description = "视频时长（秒）", example = "120")
    private Integer duration;

    @Schema(description = "媒体访问URL", example = "https://storage.com/media/sunset.jpg")
    private String url;

    @Schema(description = "缩略图访问URL", example = "https://storage.com/thumb/sunset.jpg")
    private String thumbnailUrl;

    @Schema(description = "创建时间", example = "2023-07-15T10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-07-15T10:35:00")
    private LocalDateTime updateTime;
}