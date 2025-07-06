package com.yu.fusionbase.web.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "媒体上传请求")
public class MediaUploadDTO {

    @Schema(description = "所属相册ID", required = true, example = "album_12345")
    private String albumId;

    @Schema(description = "文件类型（image/video）", required = true, example = "image")
    private String fileType;
}