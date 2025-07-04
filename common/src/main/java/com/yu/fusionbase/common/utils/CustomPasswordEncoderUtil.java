package com.yu.fusionbase.common.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 自定义密码加密工具类 (SHA-256加盐哈希)
 */
@Component
public class CustomPasswordEncoderUtil implements PasswordEncoder {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 盐值长度
    private static final int ITERATIONS = 10000; // 哈希迭代次数

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            // 1. 生成随机盐值
            byte[] salt = generateSalt();
            
            // 2. 计算密码哈希值
            byte[] hash = calculateHash(rawPassword.toString(), salt);
            
            // 3. 组合格式: 算法$迭代次数$Base64(盐值)$Base64(哈希值)
            return ALGORITHM + 
                   "$" + ITERATIONS + 
                   "$" + Base64.getEncoder().encodeToString(salt) + 
                   "$" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password encoding failed", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            // 1. 解析加密字符串
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 4) return false;
            
            // 2. 获取参数
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] originalHash = Base64.getDecoder().decode(parts[3]);
            
            // 3. 计算输入密码的哈希值
            byte[] inputHash = calculateHash(rawPassword.toString(), salt, iterations);
            
            // 4. 安全比较哈希值
            return MessageDigest.isEqual(inputHash, originalHash);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private byte[] calculateHash(String password, byte[] salt) throws Exception {
        return calculateHash(password, salt, ITERATIONS);
    }

    private byte[] calculateHash(String password, byte[] salt, int iterations) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        
        // 初始哈希: 密码 + 盐
        digest.update(password.getBytes(StandardCharsets.UTF_8));
        digest.update(salt);
        byte[] hash = digest.digest();
        
        // 多次迭代增强安全性
        for (int i = 0; i < iterations - 1; i++) {
            digest.reset();
            hash = digest.digest(hash);
        }
        
        return hash;
    }
}