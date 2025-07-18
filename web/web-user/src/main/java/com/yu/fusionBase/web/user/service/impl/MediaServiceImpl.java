package com.yu.fusionBase.web.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.fusionBase.common.exception.FusionBaseException;
import com.yu.fusionBase.common.minio.MinioProperties;
import com.yu.fusionBase.common.utils.IdGenerator;
import com.yu.fusionBase.common.utils.LogUtil;
import com.yu.fusionBase.model.entity.Media;
import com.yu.fusionBase.model.enums.FileType;
import com.yu.fusionBase.web.user.dto.response.MediaVO;
import com.yu.fusionBase.web.user.mapper.MediaMapper;
import com.yu.fusionBase.web.user.service.AlbumService;
import com.yu.fusionBase.web.user.service.MediaService;
import com.yu.fusionBase.web.user.utils.Util;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaMapper,Media> implements MediaService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final MediaMapper mediaMapper;
    private final IdGenerator idGenerator;
    private final AlbumService albumService;

    // 缩略图配置
    private static final int THUMB_WIDTH = 320;
    private static final int THUMB_HEIGHT = 240;
    private static final String THUMB_DIR = "thumbnails/";
    private static final String THUMB_SUFFIX = "_thumb";
    private static final String VIDEO_THUMBNAIL_FORMAT = "jpg";

    @Override
    public MediaVO uploadMedia(String albumId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        albumService.getAlbumById(albumId);

        String userId = Util.getCurrentUserId();
        String mediaId = idGenerator.nextId();
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = FilenameUtils.getExtension(originalFilename);

        String objectName = String.format("%s/%s/%s.%s",
                userId, LocalDateTime.now().toLocalDate().toString(), mediaId, extension);

        // 读取文件内容到字节数组（用于缩略图生成）
        //byte[] fileBytes = file.getBytes();

        // 上传到MinIO
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        String currentUserId = Util.getCurrentUserId();

        ThumbnailInfo thumbnailInfo = null;
        if (isImageFile(file) || isVideoFile(file)) {
            try (InputStream thumbStream = file.getInputStream()) {
                thumbnailInfo = generateThumbnail(mediaId, thumbStream, file.getContentType(), objectName, extension);
            }
        }

        String thumbnailPath = thumbnailInfo.getThumbnailPath() != null ? thumbnailInfo.getThumbnailPath() : "";
        Integer duration = thumbnailInfo.getDuration() != null ? thumbnailInfo.getDuration() : 0;

        Media media = new Media();
        media.setMediaId(mediaId);
        media.setAlbumId(albumId);
        media.setUserId(currentUserId);
        media.setFileName(originalFilename);
        media.setStoragePath(objectName);
        media.setThumbnailPath(thumbnailPath);//缩略图
        media.setDuration(duration);
        media.setFileType(FileType.valueOf(file.getContentType().split("/")[0].toLowerCase()));
        media.setFileSize(file.getSize());
        media.setUpdateTime(new Date());

        save(media);

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
    public Boolean deleteMedia(String mediaId) {
        Media media = mediaMapper.selectById(mediaId);
        if (media == null) {
            return false;
        }

        return removeById(mediaId);
    }

    @Override
    public List<MediaVO> getAlbumMedia(String albumId) {
        // 使用MyBatis-Plus的lambda查询
        List<Media> mediaList = lambdaQuery()
                .eq(Media::getAlbumId, albumId)
                .list();
        return mediaList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @SneakyThrows
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

    private String getFileUrl(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .expiry(7, TimeUnit.DAYS)  // 7天有效期
                        .build()
        );
    }

    /**
     * 生成缩略图（支持图片和视频）
     */
    public ThumbnailInfo generateThumbnail(String mediaId, InputStream thumbStream, String contentType, String objectName, String extension) {
        try {
            if (isImage(contentType)) {
                return generateImageThumbnail(thumbStream, objectName, extension);
            } else if (isVideo(contentType)) {
                return generateVideoThumbnail(thumbStream, objectName);
            }
        } catch (Exception e) {
            LogUtil.error("缩略图生成失败，mediaId: {}, 错误信息: {}", mediaId, e.getMessage(), e);
        }
        return new ThumbnailInfo("", 0);
    }

    /**
     * 生成图片缩略图
     */
    private ThumbnailInfo generateImageThumbnail(InputStream inputStream, String objectName, String extension)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String thumbObjectName = THUMB_DIR + objectName.replace(".", THUMB_SUFFIX + ".");

        try (ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream()) {

            // 使用Thumbnailator生成缩略图
            Thumbnails.of(inputStream)
                    .size(THUMB_WIDTH, THUMB_HEIGHT)
                    .crop(Positions.CENTER)
                    .outputFormat(extension)
                    .toOutputStream(thumbOutput);

            // 上传缩略图到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(thumbObjectName)
                            .stream(
                                    new ByteArrayInputStream(thumbOutput.toByteArray()),
                                    thumbOutput.size(),
                                    -1
                            )
                            .contentType("image/" + extension)
                            .build()
            );

            return new ThumbnailInfo(thumbObjectName, 0);
        }
    }

    /**
     * 生成视频缩略图（使用FFmpeg）
     */
    private ThumbnailInfo generateVideoThumbnail(InputStream inputStream, String objectName)
            throws IOException, InterruptedException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // 创建临时文件（自动删除）
        File tempInput = null;
        File tempOutput = null;
        try {
            tempInput = File.createTempFile("video-", ".tmp");
            tempOutput = File.createTempFile("thumb-", "." + VIDEO_THUMBNAIL_FORMAT);

            // 写入临时文件
            Files.copy(inputStream, tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 1. 获取视频时长
            double durationInSeconds = getVideoDuration(tempInput);
            int durationMs = (int) (durationInSeconds * 1000);

            // 2. 计算缩略图截取时间点（取1/10处避免黑屏，至少0.5秒）
            double thumbnailTime = Math.max(durationInSeconds * 0.1, 0.5);

            // 3. 生成缩略图
            generateThumbnail(tempInput, tempOutput, thumbnailTime);

            // 4. 读取缩略图
            byte[] thumbnailBytes = Files.readAllBytes(tempOutput.toPath());
            String thumbObjectName = buildThumbObjectName(objectName);

            // 5. 上传到MinIO
            uploadToMinio(thumbObjectName, thumbnailBytes);

            return new ThumbnailInfo(thumbObjectName, durationMs);
        } finally {
            // 确保删除临时文件
            deleteTempFile(tempInput);
            deleteTempFile(tempOutput);
        }
    }

    // 辅助方法：获取视频时长
    private double getVideoDuration(File videoFile) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "csv=p=0",  // 简化输出格式
                videoFile.getAbsolutePath()
        ).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.readLine();
            String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);

            int exitCode = process.waitFor();
            process.destroy();
            if (exitCode != 0 || output == null) {
                throw new IOException("ffprobe failed: " + error);
            }
            return Double.parseDouble(output.trim());
        }
    }

    // 辅助方法：生成缩略图
    private void generateThumbnail(File input, File output, double timestamp)
            throws IOException, InterruptedException {

        Process process = new ProcessBuilder(
                "ffmpeg",
                "-ss", String.valueOf(timestamp), // 动态时间点
                "-i", input.getAbsolutePath(),
                "-vframes", "1",
                "-vf", "scale=" + THUMB_WIDTH + ":" + THUMB_HEIGHT + ":force_original_aspect_ratio=decrease",
                "-y",
                output.getAbsolutePath()
        ).start();

        String errorOutput = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        process.destroy();
        if (exitCode != 0) {
            throw new IOException("ffmpeg failed: " + errorOutput);
        }
    }

    // 辅助方法：构建对象名
    private String buildThumbObjectName(String originalName) {
        String baseName = originalName.replaceFirst("\\.[^.]+$", "");
        return THUMB_DIR + baseName + THUMB_SUFFIX + "." + VIDEO_THUMBNAIL_FORMAT;
    }

    // 辅助方法：上传到MinIO
    private void uploadToMinio(String objectName, byte[] data) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException,
            NoSuchAlgorithmException, ServerException, InternalException,
            XmlParserException, ErrorResponseException {

        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(stream, data.length, -1)
                            .contentType("image/" + VIDEO_THUMBNAIL_FORMAT)
                            .build());
        }
    }

    // 辅助方法：安全删除临时文件
    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                LogUtil.error("临时文件删除失败: {} - {}", file.getAbsolutePath(), e.getMessage());
            }
        }
    }

    // 判断是否为图片文件
    private boolean isImageFile(MultipartFile file) {
        return isImage(file.getContentType());
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    // 判断是否为视频文件
    private boolean isVideoFile(MultipartFile file) {
        return isVideo(file.getContentType());
    }

    private boolean isVideo(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }

    // 内部静态类
    @Getter
    public static class ThumbnailInfo {
        // getter和setter
        private String thumbnailPath; // 缩略图路径
        private Integer duration; // 视频时长（毫秒，图片为null）

        public ThumbnailInfo(String thumbnailPath, Integer duration) {
            this.thumbnailPath = thumbnailPath;
            this.duration = duration;
        }

    }
}