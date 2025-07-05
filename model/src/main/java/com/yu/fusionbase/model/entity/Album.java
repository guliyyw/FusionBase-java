package com.yu.fusionbase.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("album")
@Schema(description = "相册信息表")
public class Album extends BaseEntity {

    @Schema(description = "相册ID（随机uint）")
    @TableId(value = "album_id", type = IdType.INPUT)
    private Long albumId;

    @Schema(description = "所有者用户ID")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "相册名称", example = "我的旅行相册")
    @TableField(value = "name")
    private String name;

    @Schema(description = "相册描述")
    @TableField(value = "description")
    private String description;

    @Schema(description = "封面图媒体ID")
    @TableField(value = "cover_media_id")
    private Long coverMediaId;

    @Schema(description = "是否公开相册", example = "false")
    @TableField(value = "is_public")
    private Boolean isPublic;
}