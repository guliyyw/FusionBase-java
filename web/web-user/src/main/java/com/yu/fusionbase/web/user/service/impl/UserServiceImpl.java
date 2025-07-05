package com.yu.fusionbase.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.login.LoginUser;
import com.yu.fusionbase.common.login.LoginUserHolder;
import com.yu.fusionbase.common.result.ResultCodeEnum;
import com.yu.fusionbase.common.utils.JwtUtil;
import com.yu.fusionbase.common.utils.IdGenerator;
import com.yu.fusionbase.web.user.mapper.UserMapper;
import com.yu.fusionbase.model.entity.User;
import com.yu.fusionbase.web.user.dto.request.UserLoginDTO;
import com.yu.fusionbase.web.user.dto.response.UserVO;
import com.yu.fusionbase.web.user.service.UserService;
import com.yu.fusionbase.common.utils.CustomPasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final CustomPasswordEncoderUtil passwordEncoder;
    private final IdGenerator idGenerator;

    @Override
    @Transactional
    public UserVO register(UserLoginDTO dto) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail())
                .eq(User::getIsDeleted, 0));

        if (count > 0) {
            throw new FusionBaseException(ResultCodeEnum.USER_EMAIL_EXIST);
        }

        User user = new User();
        user.setUserId(idGenerator.nextId()); // 使用ID生成器
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPassword(dto.getPassword());
        user.setUsername(dto.getEmail().split("@")[0]);

        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail())
                .eq(User::getIsDeleted, 0));

        if (user == null) {
            throw new FusionBaseException(ResultCodeEnum.USER_NOT_EXIST);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new FusionBaseException(ResultCodeEnum.PASSWORD_ERROR);
        }

        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        return JwtUtil.createToken(user.getUserId(), user.getEmail());
    }

    @Override
    public UserVO getCurrentUser() {
        String userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDeleted() != 0) {
            throw new FusionBaseException(ResultCodeEnum.USER_NOT_EXIST);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateUser(UserVO userVO) {
        String userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDeleted() != 0) {
            throw new FusionBaseException(ResultCodeEnum.USER_NOT_EXIST);
        }

        if (userVO.getUsername() != null) {
            user.setUsername(userVO.getUsername());
        }
        if (userVO.getAvatarUrl() != null) {
            user.setAvatarPath(userVO.getAvatarUrl());
        }

        userMapper.updateById(user);
        return convertToVO(user);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setUserId(user.getUserId());
        vo.setAvatarUrl(user.getAvatarPath());
        vo.setCreatedTime(convertToLocalDateTime(user.getCreateTime()));
        vo.setLastLogin(user.getLastLogin());
        return vo;
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String getCurrentUserId() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null) {
            throw new FusionBaseException(ResultCodeEnum.UNAUTHORIZED);
        }
        return loginUser.getUserId();
    }
}