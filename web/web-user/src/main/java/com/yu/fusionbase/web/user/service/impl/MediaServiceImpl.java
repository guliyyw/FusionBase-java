package com.yu.fusionbase.web.user.service.impl;

import com.yu.fusionbase.common.minio.MinioProperties;
import com.yu.fusionbase.common.utils.IdGenerator;
import com.yu.fusionbase.model.entity.Media;
import com.yu.fusionbase.model.enums.FileType;
import com.yu.fusionbase.web.user.dto.request.MediaUploadDTO;
import com.yu.fusionbase.web.user.dto.response.MediaVO;
import com.yu.fusionbase.web.user.mapper.MediaMapper;
import com.yu.fusionbase.web.user.service.AlbumService;
import com.yu.fusionbase.web.user.service.MediaService;
import com.yu.fusionbase.web.user.utils.Util;
import io.minio.*;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final MediaMapper mediaMapper;
    private final IdGenerator idGenerator;
    private final AlbumService albumService;

    @Override
    public MediaVO uploadMedia(String albumId, MultipartFile file, MediaUploadDTO dto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        albumService.getAlbumById(albumId);

        String mediaId = idGenerator.nextId();
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = FilenameUtils.getExtension(originalFilename);
        String objectName = String.format("%s/%s.%s",
                LocalDateTime.now().toLocalDate().toString(), mediaId, extension);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        String currentUserId = Util.getCurrentUserId();

        Media media = new Media();
        BeanUtils.copyProperties(dto,media);
        media.setMediaId(mediaId);
        media.setAlbumId(albumId);
        media.setUserId(currentUserId);
        media.setFileName(originalFilename);
        media.setStoragePath(objectName);
        media.setThumbnailPath("");
        media.setFileType(FileType.valueOf(file.getContentType().split("/")[0].toLowerCase()));
        media.setFileSize(file.getSize());
        media.setUpdateTime(new Date());

        mediaMapper.insert(media);

        return convertToVO(media);
    }

    @Override
    public void downloadMedia(String mediaId, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 1. 查询媒体信息
        Media media = mediaMapper.selectById(mediaId);
        if (media == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // 2. 设置响应头
        //response.setContentType(String.valueOf(media.getFileType()));
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + media.getFileName() + "\"");

        // 3. 流式传输
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(media.getStoragePath())
                        .build()
        )) {
            // 使用高效的文件流复制
            byte[] buffer = new byte[1024 * 8]; // 8KB缓冲区
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            response.flushBuffer();
        }
    }

    @Override
    public Boolean deleteMedia(String mediaId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Media media = mediaMapper.selectById(mediaId);
        if (media == null) {
            return false;
        }

        return mediaMapper.deleteById(media) > 0;
    }

    @Override
    public List<MediaVO> getAlbumMedia(String albumId) {
        List<Media> mediaList = mediaMapper.selectByAlbumId(albumId);

        // 2. 转换为VO列表
        return mediaList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private MediaVO convertToVO(Media media) {
        MediaVO vo = new MediaVO();
        BeanUtils.copyProperties(media, vo);
        // 生成文件访问URL
        vo.setUrl(getFileUrl(media.getStoragePath()));
        if (media.getThumbnailPath() != null && !media.getThumbnailPath().isEmpty()) {
            vo.setThumbnailUrl(getFileUrl(media.getThumbnailPath()));
        }
        return vo;
    }

    private String getFileUrl(String objectName) {
        return String.format("%s/%s/%s",
                minioProperties.getEndpoint(),
                minioProperties.getBucketName(),
                objectName);
    }
}