package com.yu.fusionbase.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.fusionbase.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} AND is_deleted = 0")
    int countByEmail(@Param("email") String email);
}