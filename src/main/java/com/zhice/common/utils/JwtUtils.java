package com.zhice.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类
 */
public class JwtUtils {

    // 密钥必须大于 256 bits (32 字节)
    private static final String SECRET_KEY_STRING = "ZhiCePlatformSecretKeyMustBeLongEnough2026!";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // 过期时间：7天
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 根据用户 ID 生成 Token
     */
    public static String generateToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 将用户 ID 存入 subject
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 Token 获取用户 ID
     * @return 如果解析失败或过期返回 null，否则返回 userId
     */
    public static Long parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            // Token 过期或被篡改
            return null;
        }
    }
}