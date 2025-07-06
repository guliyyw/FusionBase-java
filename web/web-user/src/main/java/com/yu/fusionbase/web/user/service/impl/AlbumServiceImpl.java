package com.yu.fusionbase.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.result.ResultCodeEnum;
import com.yu.fusionbase.common.utils.IdGenerator;
import com.yu.fusionbase.common.utils.LogUtil;
import com.yu.fusionbase.model.entity.Album;
import com.yu.fusionbase.model.entity.AlbumShare;
import com.yu.fusionbase.model.enums.PermissionLevel;
import com.yu.fusionbase.web.user.dto.request.AlbumCreateDTO;
import com.yu.fusionbase.web.user.dto.request.AlbumShareDTO;
import com.yu.fusionbase.web.user.dto.response.AlbumVO;
import com.yu.fusionbase.web.user.mapper.MediaMapper;
import com.yu.fusionbase.web.user.service.AlbumService;
import com.yu.fusionbase.web.user.mapper.AlbumMapper;
import com.yu.fusionbase.web.user.mapper.AlbumShareMapper;
import com.yu.fusionbase.web.user.utils.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final AlbumShareMapper albumShareMapper;
    private final MediaMapper mediaMapper;
    private final IdGenerator idGenerator;

    @Override
    @Transactional
    public AlbumVO createAlbum(AlbumCreateDTO dto) {
        String userId = Util.getCurrentUserId();

        Album album = new Album();
        BeanUtils.copyProperties(dto, album);
        album.setUserId(userId);
        album.setAlbumId(idGenerator.nextId()); // 使用ID生成器
        album.setUpdateTime(new Date());

        albumMapper.insert(album);
        return convertToVO(album);
    }

    @Override
    public List<AlbumVO> getUserAlbums() {
        String userId = Util.getCurrentUserId();
        List<Album> albums = albumMapper.selectList(
                new LambdaQueryWrapper<Album>()
                        .eq(Album::getUserId, userId)
                        .eq(Album::getIsDeleted, 0)
        );
        return albums.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumVO getAlbumById(String albumId) {
        Album album = albumMapper.selectById(albumId);
        if (album == null || album.getIsDeleted() != 0) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        checkAlbumPermission(album, PermissionLevel.VIEWER);
        return convertToVO(album);
    }

    @Override
    @Transactional
    public AlbumVO updateAlbum(String albumId, AlbumCreateDTO dto) {
        Album album = albumMapper.selectById(albumId);
        if (album == null || album.getIsDeleted() != 0) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        checkAlbumPermission(album, PermissionLevel.MANAGER);
        BeanUtils.copyProperties(dto, album);

        albumMapper.updateById(album);
        return convertToVO(album);
    }

    @Override
    @Transactional
    public Boolean deleteAlbum(String albumId) {
        Album album = albumMapper.selectById(albumId);
        if (album == null || album.getIsDeleted() != 0) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        if (!Util.getCurrentUserId().equals(album.getUserId())) {
            throw new FusionBaseException(ResultCodeEnum.PERMISSION_DENIED);
        }

        return albumMapper.deleteById(album) > 0;
    }

    @Override
    @Transactional
    public Boolean shareAlbum(String albumId, AlbumShareDTO dto) {
        Album album = albumMapper.selectById(albumId);
        if (album == null || album.getIsDeleted() != 0) {
            throw new FusionBaseException(ResultCodeEnum.ALBUM_NOT_FOUND);
        }

        if (!Util.getCurrentUserId().equals(album.getUserId())) {
            checkAlbumPermission(album, PermissionLevel.MANAGER);
        }

        AlbumShare share = new AlbumShare();
        share.setShareId(idGenerator.nextId()); // 使用ID生成器
        share.setAlbumId(albumId);
        share.setOwnerUserId(album.getUserId());

        if (dto.getSharedUserId() != null) {
            share.setSharedUserId(dto.getSharedUserId());
        } else if (dto.getInviteeEmail() != null) {
            LogUtil.info("Inviting user by email: {}", dto.getInviteeEmail());
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
        String userId = Util.getCurrentUserId();
        List<Album> albums = albumMapper.selectSharedAlbumsByUserId(userId);
        return albums.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private AlbumVO convertToVO(Album album) {
        AlbumVO vo = new AlbumVO();
        BeanUtils.copyProperties(album, vo);
        vo.setAlbumId(album.getAlbumId());
        vo.setCreatedTime(convertToLocalDateTime(album.getCreateTime()));
        vo.setUpdatedTime(convertToLocalDateTime(album.getUpdateTime()));

        if (Objects.equals(album.getUserId(), Util.getCurrentUserId())) {
            vo.setPermission(PermissionLevel.MANAGER);
        } else {
            PermissionLevel permission = getSharedPermission(album.getAlbumId());
            vo.setPermission(permission);
        }
        int count = mediaMapper.countByAlbumId(album.getAlbumId());
        vo.setMediaCount(count);

        return vo;
    }

    private void checkAlbumPermission(Album album, PermissionLevel requiredLevel) {
        String currentUserId = Util.getCurrentUserId();

        if (Objects.equals(album.getUserId(), currentUserId)) {
            return;
        }

        if (album.getIsPublic() && requiredLevel == PermissionLevel.VIEWER) {
            return;
        }

        PermissionLevel permission = getSharedPermission(album.getAlbumId());
        if (permission == null || permission.ordinal() < requiredLevel.ordinal()) {
            throw new FusionBaseException(ResultCodeEnum.PERMISSION_DENIED);
        }
    }

    private PermissionLevel getSharedPermission(String albumId) {
        String userId = Util.getCurrentUserId();
        AlbumShare share = albumShareMapper.selectByAlbumAndUser(albumId, userId);
        return share != null ? share.getPermissionLevel() : null;
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}