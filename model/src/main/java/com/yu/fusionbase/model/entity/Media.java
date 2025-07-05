package com.yu.fusionbase.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yu.fusionbase.model.enums.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("media")
@Schema(description = "媒体文件表")
public class Media extends BaseEntity {

    @Schema(description = "媒体ID（随机uint）")
    @TableId(value = "media_id", type = IdType.INPUT)
    private String mediaId;

    @Schema(description = "所属相册ID")
    @TableField(value = "album_id")
    private String albumId;

    @Schema(description = "上传用户ID")
    @TableField(value = "user_id")
    private String userId;

    @Schema(description = "原始文件名")
    @TableField(value = "file_name")
    private String fileName;

    @Schema(description = "存储路径")
    @TableField(value = "storage_path")
    private String storagePath;

    @Schema(description = "缩略图存储路径")
    @TableField(value = "thumbnail_path")
    private String thumbnailPath;

    @Schema(description = "文件类型", example = "image/video")
    @TableField(value = "file_type")
    private FileType fileType;

    @Schema(description = "文件大小(字节)")
    @TableField(value = "file_size")
    private Long fileSize;

    @Schema(description = "图片/视频宽度")
    @TableField(value = "width")
    private Integer width;

    @Schema(description = "图片/视频高度")
    @TableField(value = "height")
    private Integer height;

    @Schema(description = "视频时长(秒)")
    @TableField(value = "duration")
    private Integer duration;
}