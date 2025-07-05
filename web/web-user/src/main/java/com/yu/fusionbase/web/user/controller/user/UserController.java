package com.yu.fusionbase.web.user.controller.user;

import com.yu.fusionbase.common.result.Result;
import com.yu.fusionbase.web.user.dto.request.UserLoginDTO;
import com.yu.fusionbase.web.user.dto.response.UserVO;
import com.yu.fusionbase.web.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户注册、登录和个人信息管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "创建新用户账户")
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserLoginDTO dto) {
        return Result.ok(userService.register(dto));
    }

    @Operation(summary = "用户登录", description = "用户登录获取JWT令牌")
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO dto) {
        return Result.ok(userService.login(dto));
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        return Result.ok(userService.getCurrentUser());
    }

    @Operation(summary = "更新用户信息", description = "更新当前用户的基本信息")
    @PutMapping("/me")
    public Result<UserVO> updateUser(@RequestBody UserVO userVO) {
        return Result.ok(userService.updateUser(userVO));
    }
}