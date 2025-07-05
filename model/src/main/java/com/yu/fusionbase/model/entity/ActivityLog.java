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
@TableName("activity_log")
@Schema(description = "系统活动日志表")
public class ActivityLog extends BaseEntity {

    @Schema(description = "日志ID（随机uint）")
    @TableId(value = "log_id", type = IdType.INPUT)
    private String logId;

    @Schema(description = "操作用户ID")
    @TableField(value = "user_id")
    private String userId;

    @Schema(description = "相关相册ID")
    @TableField(value = "album_id")
    private String albumId;

    @Schema(description = "相关媒体ID")
    @TableField(value = "media_id")
    private String mediaId;

    @Schema(description = "活动类型", example = "UPLOAD_MEDIA")
    @TableField(value = "activity_type")
    private String activityType;

    @Schema(description = "活动详情")
    @TableField(value = "activity_details")
    private String activityDetails;

    @Schema(description = "操作IP地址")
    @TableField(value = "ip_address")
    private String ipAddress;

    @Schema(description = "用户代理信息")
    @TableField(value = "user_agent")
    private String userAgent;
}