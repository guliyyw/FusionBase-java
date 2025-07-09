package com.yu.fusionbase.web.user.service;

import com.yu.fusionbase.web.user.dto.request.MediaUploadDTO;
import com.yu.fusionbase.web.user.dto.response.MediaVO;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface MediaService {
    MediaVO uploadMedia(String albumId, MultipartFile file, MediaUploadDTO dto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    void downloadMedia(String mediaId, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    Boolean deleteMedia(String mediaId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    List<MediaVO> getAlbumMedia(String albumId);
}