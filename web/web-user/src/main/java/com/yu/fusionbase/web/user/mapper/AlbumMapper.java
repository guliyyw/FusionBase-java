package com.yu.fusionbase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionbase.model.entity.Album;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AlbumMapper extends BaseMapper<Album> {

    @Select("SELECT a.* FROM album a " +
            "JOIN album_share s ON a.albumId = s.albumId " +
            "WHERE s.shared_userId = #{userId} AND a.is_deleted = 0 AND (s.expires_at IS NULL OR s.expires_at > NOW())")
    List<Album> selectSharedAlbumsByUserId(@Param("userId") String userId);
}