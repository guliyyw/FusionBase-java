package com.yu.fusionbase.web.user.service;

import com.yu.fusionbase.web.user.dto.request.UserLoginDTO;
import com.yu.fusionbase.web.user.dto.response.UserVO;

public interface UserService {

    UserVO register(UserLoginDTO dto);

    String login(UserLoginDTO dto);

    UserVO getCurrentUser();

    UserVO updateUser(UserVO userVO);
}