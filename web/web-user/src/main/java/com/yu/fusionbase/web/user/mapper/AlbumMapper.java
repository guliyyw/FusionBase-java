package com.yu.fusionbase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionbase.model.entity.Album;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AlbumMapper extends BaseMapper<Album> {

    @Select("SELECT a.* FROM album a " +
            "JOIN album_share s ON a.id = s.album_id " +
            "WHERE s.shared_user_id = #{userId} AND a.deleted_at IS NULL AND s.expires_at > NOW()")
    List<Album> selectSharedAlbumsByUserId(@Param("userId") Long userId);
}