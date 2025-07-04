package com.yu.fusionbase.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.result.ResultCodeEnum;
import com.yu.fusionbase.common.utils.JwtUtil;

import com.yu.fusionbase.model.entity.Album;
import com.yu.fusionbase.model.entity.AlbumShare;
import com.yu.fusionbase.model.entity.User;
import com.yu.fusionbase.model.enums.PermissionLevel;
import com.yu.fusionbase.web.user.dto.request.AlbumCreateDTO;
import com.yu.fusionbase.web.user.dto.request.AlbumShareDTO;
import com.yu.fusionbase.web.user.dto.response.AlbumVO;
import com.yu.fusionbase.web.user.service.AlbumService;
import com.yu.fusionbase.web.user.mapper.AlbumMapper;
import com.yu.fusionbase.web.user.mapper.AlbumShareMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final AlbumShareMapper albumShareMapper;

    @Override
    @Transactional
    public AlbumVO createAlbum(AlbumCreateDTO dto) {
        Long userId = getCurrentUserId();

        Album album = new Album();
        BeanUtils.copyProperties(dto, album);
        album.setUserId(userId);

        albumMapper.insert(album);
        return convertToVO(album);
    }

    @Override
    public List<AlbumVO> getUserAlbums() {
        Long userId = getCurrentUserId();
        List<Album> albums = albumMapper.selectList(
                new LambdaQueryWrapper<Album>()
                        .eq(Album::getUserId, userId)
                        .isNull(Album::getIsDeleted)
        );
        return albums.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumVO getAlbumById(Long id) {
        Album album = albumMapper.selectById(id);
        if (album == null || album.getIsDeleted() != null) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        // 检查权限
        checkAlbumPermission(album, PermissionLevel.VIEWER);

        return convertToVO(album);
    }

    @Override
    @Transactional
    public AlbumVO updateAlbum(Long id, AlbumCreateDTO dto) {
        Album album = albumMapper.selectById(id);
        if (album == null || album.getIsDeleted() != null) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        // 检查权限（需要管理者或所有者）
        checkAlbumPermission(album, PermissionLevel.MANAGER);

        BeanUtils.copyProperties(dto, album);
        albumMapper.updateById(album);
        return convertToVO(album);
    }

    @Override
    @Transactional
    public Boolean deleteAlbum(Long id) {
        Album album = albumMapper.selectById(id);
        if (album == null || album.getIsDeleted() != null) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        // 只有所有者可以删除
        if (!getCurrentUserId().equals(album.getUserId())) {
            throw new FusionBaseException(ResultCodeEnum.PERMISSION_DENIED);
        }

        // 软删除相册
        album.setIsDeleted((byte) 1);
        album.setUpdateTime(new Date());
        return albumMapper.updateById(album) > 0;
    }

    @Override
    @Transactional
    public Boolean shareAlbum(Long albumId, AlbumShareDTO dto) {
        Album album = albumMapper.selectById(albumId);
        if (album == null || album.getIsDeleted() != null) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        // 检查当前用户是否有共享权限（必须是所有者或管理者）
        if (!getCurrentUserId().equals(album.getUserId())) {
            checkAlbumPermission(album, PermissionLevel.MANAGER);
        }

        // 创建共享记录
        AlbumShare share = new AlbumShare();
        share.setAlbumId(albumId);
        share.setOwnerId(album.getUserId());

        if (dto.getSharedUserId() != null) {
            share.setSharedUserId(dto.getSharedUserId());
        } else if (dto.getInviteeEmail() != null) {
            // 处理邀请未注册用户的情况
            // 这里需要调用邀请服务，实际项目中会发送邀请邮件
            // 此处简化处理，只记录日志
            System.out.println("Inviting user by email: " + dto.getInviteeEmail());
            // 设置共享用户ID为null，表示待接受邀请
            share.setSharedUserId(null);
        } else {
            throw new FusionBaseException(ResultCodeEnum.PARAM_ERROR);
        }

        share.setPermissionLevel(dto.getPermissionLevel());
        share.setExpiresAt(dto.getExpiresAt());

        albumShareMapper.insert(share);
        return true;
    }

    @Override
    public List<AlbumVO> getSharedAlbums() {
        Long userId = getCurrentUserId();
        List<Album> albums = albumMapper.selectSharedAlbumsByUserId(userId);
        return albums.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private AlbumVO convertToVO(Album album) {
        AlbumVO vo = new AlbumVO();
        BeanUtils.copyProperties(album, vo);
        vo.setAlbumId(album.getId());

        // 设置权限
        if (Objects.equals(album.getUserId(), getCurrentUserId())) {
            vo.setPermission(PermissionLevel.MANAGER);
        } else {
            // 查询共享权限
            PermissionLevel permission = getSharedPermission(album.getId());
            vo.setPermission(permission);
        }

        // 设置媒体数量（实际项目中需要查询媒体表）
        vo.setMediaCount(0);

        return vo;
    }

    private void checkAlbumPermission(Album album, PermissionLevel requiredLevel) {
        Long currentUserId = getCurrentUserId();

        // 所有者拥有所有权限
        if (Objects.equals(album.getUserId(), currentUserId)) {
            return;
        }

        // 公开相册允许查看
        if (album.getIsPublic() && requiredLevel == PermissionLevel.VIEWER) {
            return;
        }

        PermissionLevel permission = getSharedPermission(album.getId());
        if (permission == null || permission.ordinal() < requiredLevel.ordinal()) {
            throw new FusionBaseException(ResultCodeEnum.PERMISSION_DENIED);
        }
    }

    private PermissionLevel getSharedPermission(Long albumId) {
        Long userId = getCurrentUserId();
        AlbumShare share = albumShareMapper.selectByAlbumAndUser(albumId, userId);
        return share != null ? share.getPermissionLevel() : null;
    }

    private Long getCurrentUserId() {
        // 从JWT中获取当前用户ID
        // 实际项目中需要从SecurityContext获取
        return 1L; // 简化处理，实际项目中需要实现
    }


}