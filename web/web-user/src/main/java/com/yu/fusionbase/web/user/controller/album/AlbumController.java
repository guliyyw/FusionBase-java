package com.yu.fusionbase.web.user.controller.album;

import com.yu.fusionbase.common.result.Result;
import com.yu.fusionbase.web.user.dto.request.AlbumCreateDTO;
import com.yu.fusionbase.web.user.dto.request.AlbumShareDTO;
import com.yu.fusionbase.web.user.dto.response.AlbumVO;
import com.yu.fusionbase.web.user.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "相册管理", description = "相册创建、编辑、共享和查看")
@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "创建相册", description = "创建新的个人相册")
    @PostMapping
    public Result<AlbumVO> createAlbum(@RequestBody AlbumCreateDTO dto) {
        return Result.ok(albumService.createAlbum(dto));
    }

    @Operation(summary = "获取用户相册列表", description = "获取当前用户的所有相册")
    @GetMapping
    public Result<List<AlbumVO>> getUserAlbums() {
        return Result.ok(albumService.getUserAlbums());
    }

    @Operation(summary = "获取相册详情", description = "根据ID获取相册详细信息")
    @GetMapping("/{id}")
    public Result<AlbumVO> getAlbumById(@PathVariable String id) {
        return Result.ok(albumService.getAlbumById(id));
    }

    @Operation(summary = "更新相册信息", description = "更新相册名称和描述")
    @PutMapping("/{id}")
    public Result<AlbumVO> updateAlbum(
            @PathVariable String id,
            @RequestBody AlbumCreateDTO dto) {
        return Result.ok(albumService.updateAlbum(id, dto));
    }

    @Operation(summary = "删除相册", description = "删除指定相册（同时删除所有媒体）")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAlbum(@PathVariable String id) {
        return Result.ok(albumService.deleteAlbum(id));
    }

    @Operation(summary = "共享相册", description = "将相册共享给其他用户")
    @PostMapping("/{id}/share")
    public Result<Boolean> shareAlbum(
            @PathVariable String id,
            @RequestBody AlbumShareDTO dto) {
        return Result.ok(albumService.shareAlbum(id, dto));
    }

    @Operation(summary = "获取共享相册列表", description = "获取其他用户共享给我的相册")
    @GetMapping("/shared")
    public Result<List<AlbumVO>> getSharedAlbums() {
        return Result.ok(albumService.getSharedAlbums());
    }

}
