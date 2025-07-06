package com.yu.fusionbase.web.user.utils;

import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.login.LoginUser;
import com.yu.fusionbase.common.login.LoginUserHolder;
import com.yu.fusionbase.common.result.ResultCodeEnum;

public class Util {
    public static String getCurrentUserId() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null) {
            throw new FusionBaseException(ResultCodeEnum.UNAUTHORIZED);
        }
        return loginUser.getUserId();
    }
}
