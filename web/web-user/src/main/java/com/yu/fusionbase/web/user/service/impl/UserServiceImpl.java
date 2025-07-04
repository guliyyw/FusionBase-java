package com.yu.fusionbase.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.result.ResultCodeEnum;
import com.yu.fusionbase.common.utils.JwtUtil;
import com.yu.fusionbase.web.user.mapper.UserMapper;
import com.yu.fusionbase.model.entity.User;
import com.yu.fusionbase.web.user.dto.request.UserLoginDTO;
import com.yu.fusionbase.web.user.dto.response.UserVO;
import com.yu.fusionbase.web.user.service.UserService;
import com.yu.fusionbase.common.utils.CustomPasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final CustomPasswordEncoderUtil passwordEncoder;
    
    @Override
    @Transactional
    public UserVO register(UserLoginDTO dto) {
        // 检查邮箱是否已注册
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail())
                .isNull(User::getIsDeleted));
        
        if (count > 0) {
            throw new FusionBaseException(ResultCodeEnum.USER_EMAIL_EXIST);
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPassword(dto.getPassword());
        user.setUsername(dto.getEmail().split("@")[0]); // 默认用户名为邮箱前缀
        
        userMapper.insert(user);
        
        return convertToVO(user);
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail())
                .eq(User::getIsDeleted,0));
        
        if (user == null) {
            throw new FusionBaseException(ResultCodeEnum.USER_NOT_EXIST);
        }
        
        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new FusionBaseException(ResultCodeEnum.PASSWORD_ERROR);
        }
        
        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成JWT
        return JwtUtil.createToken(user.getId(), user.getEmail());
    }

    @Override
    public UserVO getCurrentUser() {
        // 实际项目中从SecurityContext获取当前用户
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDeleted() != null) {
            throw new FusionBaseException(ResultCodeEnum.USER_NOT_EXIST);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateUser(UserVO userVO) {
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDeleted() != null) {
            throw new FusionBaseException(ResultCodeEnum.USER_NOT_EXIST);
        }
        
        // 更新基本信息
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
        vo.setUserId(user.getId());
        vo.setAvatarUrl(user.getAvatarPath());
        return vo;
    }
    
    private Long getCurrentUserId() {
        // 实际项目中从SecurityContext获取
        return 1L; // 简化处理
    }
}