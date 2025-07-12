package com.yu.fusionBase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionBase.model.entity.Media;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

    @Update("UPDATE media SET thumbnail_path = #{thumbnailPath} WHERE media_id = #{mediaId}")
    int updateThumbnailPath(@Param("mediaId") String mediaId, @Param("thumbnailPath") String thumbnailPath);

}