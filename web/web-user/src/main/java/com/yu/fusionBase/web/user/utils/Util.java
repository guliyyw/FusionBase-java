package com.yu.fusionBase.web.user.utils;

import com.yu.fusionBase.common.exception.FusionBaseException;
import com.yu.fusionBase.common.login.LoginUser;
import com.yu.fusionBase.common.login.LoginUserHolder;
import com.yu.fusionBase.common.result.ResultCodeEnum;

public class Util {
    public static String getCurrentUserId() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null) {
            throw new FusionBaseException(ResultCodeEnum.UNAUTHORIZED);
        }
        return loginUser.getUserId();
    }
}
