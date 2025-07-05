package com.yu.fusionbase.web.user.service;

import com.yu.fusionbase.web.user.dto.request.AlbumCreateDTO;
import com.yu.fusionbase.web.user.dto.request.AlbumShareDTO;
import com.yu.fusionbase.web.user.dto.response.AlbumVO;

import java.util.List;

public interface AlbumService {

    AlbumVO createAlbum(AlbumCreateDTO dto);

    List<AlbumVO> getUserAlbums();

    AlbumVO getAlbumById(Long albumId);

    AlbumVO updateAlbum(Long albumId, AlbumCreateDTO dto);

    Boolean deleteAlbum(Long albumId);

    Boolean shareAlbum(Long albumId, AlbumShareDTO dto);

    List<AlbumVO> getSharedAlbums();
}