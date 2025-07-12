package com.yu.fusionBase.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yu.fusionBase.model.enums.InvitationStatus;
import com.yu.fusionBase.model.enums.PermissionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("share_invitation")
@Schema(description = "相册共享邀请表")
public class ShareInvitation extends BaseEntity {

    @Schema(description = "邀请ID（随机uint）")
    @TableId(value = "invitation_id", type = IdType.INPUT)
    private Long invitationId;

    @Schema(description = "相册ID")
    @TableField(value = "album_id")
    private Long albumId;

    @Schema(description = "邀请人ID")
    @TableField(value = "inviter_user_id")
    private Long inviterUserId;

    @Schema(description = "被邀请人邮箱")
    @TableField(value = "invitee_email")
    private String inviteeEmail;

    @Schema(description = "邀请令牌")
    @TableField(value = "token")
    private String token;

    @Schema(description = "权限级别")
    @TableField(value = "permission_level")
    private PermissionLevel permissionLevel;

    @Schema(description = "邀请状态")
    @TableField(value = "status")
    private InvitationStatus status;

    @Schema(description = "过期时间")
    @TableField(value = "expires_at")
    private LocalDateTime expiresAt;
}