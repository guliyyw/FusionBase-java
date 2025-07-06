package com.yu.fusionbase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionbase.model.entity.Media;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MediaMapper extends BaseMapper<Media> {

    @Select("SELECT COUNT(*) FROM fusion_base.media WHERE album_id = #{albumId} AND is_deleted = 0")
    int countByAlbumId(@Param("albumId") String albumId);

    @Select("""
        SELECT * FROM fusion_base.media 
             WHERE album_id = #{albumId}
                AND is_deleted = 0
        ORDER BY create_time DESC 
    """)
    List<Media> selectByAlbumId(String albumId);
}