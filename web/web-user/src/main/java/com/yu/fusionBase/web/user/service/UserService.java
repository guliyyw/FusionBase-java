package com.yu.fusionBase.web.user.service;

import com.yu.fusionBase.web.user.dto.request.UserLoginDTO;
import com.yu.fusionBase.web.user.dto.response.UserVO;

public interface UserService {

    UserVO register(UserLoginDTO dto);

    String login(UserLoginDTO dto);

    UserVO getCurrentUser();

    UserVO updateUser(UserVO userVO);
}