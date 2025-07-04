package com.yu.fusionbase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionbase.model.entity.AlbumShare;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AlbumShareMapper extends BaseMapper<AlbumShare> {

    @Select("SELECT * FROM album_share " +
            "WHERE album_id = #{albumId} AND shared_user_id = #{userId} " +
            "AND expires_at > NOW()")
    AlbumShare selectByAlbumAndUser(
            @Param("albumId") Long albumId, 
            @Param("userId") Long userId);
}