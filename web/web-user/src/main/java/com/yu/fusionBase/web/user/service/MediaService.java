package com.yu.fusionBase.web.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.fusionBase.model.entity.Media;
import com.yu.fusionBase.web.user.dto.response.MediaVO;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface MediaService extends IService<Media> {
    MediaVO uploadMedia(String albumId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    void downloadMedia(String mediaId, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    Boolean deleteMedia(String mediaId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    List<MediaVO> getAlbumMedia(String albumId);
}