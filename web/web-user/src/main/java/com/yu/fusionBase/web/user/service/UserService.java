package com.yu.fusionBase.web.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.fusionBase.model.entity.User;
import com.yu.fusionBase.web.user.dto.request.UserLoginDTO;
import com.yu.fusionBase.web.user.dto.response.UserVO;

public interface UserService extends IService<User> {

    UserVO register(UserLoginDTO dto);

    String login(UserLoginDTO dto);

    UserVO getCurrentUser();

    UserVO updateUser(UserVO userVO);
}