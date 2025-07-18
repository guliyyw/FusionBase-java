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

    int countByAlbumId(@Param("albumId") String albumId);

    List<Media> selectByAlbumId(@Param("albumId") String albumId);

}