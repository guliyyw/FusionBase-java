-- 禁用外键约束
SET FOREIGN_KEY_CHECKS = 0;

-- 按依赖逆序删除表
DROP TABLE IF EXISTS media_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS activity_log;
DROP TABLE IF EXISTS share_invitation;
DROP TABLE IF EXISTS album_share;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS user;

-- 启用外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 用户表
CREATE TABLE user (
                      id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                      username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                      password_hash CHAR(255) NOT NULL COMMENT '加密密码(BCrypt)',
                      password CHAR(255) NOT NULL COMMENT '密码',
                      email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
                      avatar_path VARCHAR(255) COMMENT '头像存储路径',
                      create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      last_login DATETIME COMMENT '最后登录时间',
                      is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                      INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 相册表
CREATE TABLE album (
                       id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '相册ID',
                       user_id BIGINT UNSIGNED NOT NULL COMMENT '所有者用户ID',
                       name VARCHAR(100) NOT NULL COMMENT '相册名称',
                       description TEXT COMMENT '相册描述',
                       cover_media_id BIGINT UNSIGNED COMMENT '封面图媒体ID',
                       is_public BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否公开',
                       create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                       FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';

-- 媒体文件表
CREATE TABLE media (
                       id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '媒体ID',
                       album_id BIGINT UNSIGNED NOT NULL COMMENT '所属相册ID',
                       user_id BIGINT UNSIGNED NOT NULL COMMENT '上传用户ID',
                       file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
                       storage_path VARCHAR(500) NOT NULL COMMENT '云存储路径',
                       thumbnail_path VARCHAR(500) COMMENT '缩略图路径',
                       file_type ENUM('image', 'video') NOT NULL COMMENT '文件类型',
                       file_size BIGINT UNSIGNED NOT NULL COMMENT '文件大小(字节)',
                       width SMALLINT UNSIGNED COMMENT '宽度',
                       height SMALLINT UNSIGNED COMMENT '高度',
                       duration INT UNSIGNED COMMENT '视频时长(秒)',
                       create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                       FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
                       FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体文件表';

-- 修正封面图外键（正确引用 media.id）
ALTER TABLE album ADD CONSTRAINT fk_cover_media
    FOREIGN KEY (cover_media_id) REFERENCES media(id) ON DELETE SET NULL;

-- 相册共享表
CREATE TABLE album_share (
                             id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '共享ID',
                             album_id BIGINT UNSIGNED NOT NULL COMMENT '相册ID',
                             owner_id BIGINT UNSIGNED NOT NULL COMMENT '所有者ID',
                             shared_user_id BIGINT UNSIGNED NOT NULL COMMENT '被共享用户ID',
                             permission_level ENUM('viewer', 'contributor', 'manager') NOT NULL DEFAULT 'viewer' COMMENT '权限级别',
                             create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             expires_at DATETIME COMMENT '过期时间',
                             is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                             UNIQUE KEY uniq_album_shared_user (album_id, shared_user_id),
                             FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
                             FOREIGN KEY (owner_id) REFERENCES user(id) ON DELETE CASCADE,
                             FOREIGN KEY (shared_user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册共享表';

-- 共享邀请表
CREATE TABLE share_invitation (
                                  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '邀请ID',
                                  album_id BIGINT UNSIGNED NOT NULL COMMENT '相册ID',
                                  inviter_id BIGINT UNSIGNED NOT NULL COMMENT '邀请人ID',
                                  invitee_email VARCHAR(100) NOT NULL COMMENT '被邀请人邮箱',
                                  token CHAR(36) NOT NULL COMMENT '邀请令牌',
                                  permission_level ENUM('viewer', 'contributor', 'manager') NOT NULL DEFAULT 'viewer' COMMENT '权限级别',
                                  status ENUM('pending', 'accepted', 'expired', 'revoked') NOT NULL DEFAULT 'pending' COMMENT '状态',
                                  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  expires_at DATETIME NOT NULL COMMENT '过期时间',
                                  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                                  UNIQUE KEY uniq_token (token),
                                  FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
                                  FOREIGN KEY (inviter_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享邀请表';

-- 活动日志表
CREATE TABLE activity_log (
                              id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
                              user_id BIGINT UNSIGNED COMMENT '用户ID',
                              album_id BIGINT UNSIGNED COMMENT '相册ID',
                              media_id BIGINT UNSIGNED COMMENT '媒体ID',
                              activity_type VARCHAR(50) NOT NULL COMMENT '活动类型',
                              activity_details TEXT COMMENT '活动详情',
                              ip_address VARCHAR(45) COMMENT 'IP地址',
                              user_agent VARCHAR(255) COMMENT '用户代理',
                              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                              FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
                              FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE SET NULL,
                              FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动日志表';

-- 标签表
CREATE TABLE tag (
                     id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
                     name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
                     create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                     update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                     is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                     INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 媒体标签关联表
CREATE TABLE media_tag (
                           media_id BIGINT UNSIGNED NOT NULL COMMENT '媒体ID',
                           tag_id BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                           PRIMARY KEY (media_id, tag_id),
                           FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体标签关联表';