package com.yu.fusionbase.common.utils;

import com.yu.fusionbase.common.exception.FusionBaseException;
import com.yu.fusionbase.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey secretKey = Keys.hmacShaKeyFor("mM4NDPyQ4mfCYEZxakWku8wKdmtufGJU".getBytes());

    public static String createToken(Long userId, String username) {

        //生成JWT
        //claim声明自定义字段
        String jwt = Jwts.builder()
                //有效期,单位毫秒
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                //主题
                .setSubject("LOGIN_USER")
                .claim("userId", userId)
                .claim("username", username)
                //签名
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    public static Claims parseToken(String token) {

        if (token == null) {
            throw new FusionBaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (ExpiredJwtException e) {
            throw new FusionBaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new FusionBaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(createToken(2L,"user"));
    }
}
