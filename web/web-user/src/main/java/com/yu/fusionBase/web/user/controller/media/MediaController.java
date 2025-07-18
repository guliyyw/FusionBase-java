package com.yu.fusionBase.web.user.controller.media;

import com.yu.fusionBase.web.user.dto.response.MediaVO;
import com.yu.fusionBase.web.user.service.MediaService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "相册媒体管理", description = "媒体文件的上传、下载、删除和查询")
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "上传媒体文件", description = "上传文件到指定相册")
    @PostMapping(value = "/upload/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MediaVO uploadMedia(
            @PathVariable @Parameter(description = "相册ID", required = true) String albumId,
            @RequestPart @Parameter(description = "媒体文件", required = true) MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return mediaService.uploadMedia(albumId, file);
    }

    @Operation(summary = "下载媒体文件", description = "根据媒体ID下载文件内容")
    @GetMapping("/download/{mediaId}")
    public void downloadMedia(
            @PathVariable String mediaId,
            HttpServletResponse response // 添加HttpServletResponse参数
    ) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        mediaService.downloadMedia(mediaId, response);
    }

    @Operation(summary = "删除媒体文件", description = "根据媒体ID删除文件")
    @DeleteMapping("/{mediaId}")
    public Boolean deleteMedia(@PathVariable String mediaId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return mediaService.deleteMedia(mediaId);
    }

    @Operation(summary = "获取相册媒体列表", description = "查询指定相册下的所有媒体文件")
    @GetMapping("/album/{albumId}")
    public List<MediaVO> getAlbumMedia(@PathVariable String albumId) {
        return mediaService.getAlbumMedia(albumId);
    }
}