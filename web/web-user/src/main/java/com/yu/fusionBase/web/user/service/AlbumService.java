package com.yu.fusionBase.web.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.fusionBase.model.entity.Album;
import com.yu.fusionBase.web.user.dto.request.AlbumCreateDTO;
import com.yu.fusionBase.web.user.dto.request.AlbumShareDTO;
import com.yu.fusionBase.web.user.dto.response.AlbumVO;

import java.util.List;

public interface AlbumService extends IService<Album> {
    AlbumVO createAlbum(AlbumCreateDTO dto);
    List<AlbumVO> getUserAlbums();
    AlbumVO getAlbumById(String albumId);
    AlbumVO updateAlbum(String albumId, AlbumCreateDTO dto);
    Boolean deleteAlbum(String albumId);
    Boolean shareAlbum(String albumId, AlbumShareDTO dto);
    List<AlbumVO> getSharedAlbums();
}