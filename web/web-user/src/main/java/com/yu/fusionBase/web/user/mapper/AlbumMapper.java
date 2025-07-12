package com.yu.fusionBase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionBase.model.entity.Album;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AlbumMapper extends BaseMapper<Album> {

//    @Select("SELECT a.* FROM album a " +
//            "JOIN album_share s ON a.albumId = s.albumId " +
//            "WHERE s.shared_userId = #{userId} AND a.is_deleted = 0 AND (s.expires_at IS NULL OR s.expires_at > NOW())")
    List<Album> selectSharedAlbumsByUserId(String userId);
}