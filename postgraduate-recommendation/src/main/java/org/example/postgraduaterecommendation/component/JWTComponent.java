package org.example.postgraduaterecommendation.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author wuwenjin
 */
@Slf4j
@Component    // 这是一个通用的编码解码组件
public class JWTComponent {
    // 私钥
    @Value("${my.secretkey}")// 读取配置中的信息
    private String secretkey;

    // 加密和解密使用同一个算法对象，无需反复创建
    // 希望在组件对象创建完之后再对这个属性进行初始化
    private Algorithm algorithm;

    @PostConstruct// 组件初始化后，初始化加密算法对象。无需反复创建
    private void init() {
        algorithm = Algorithm.HMAC256(secretkey);
    }

    // encode 加密
    public String encode(Map<String, Object> playload) {
        LocalDateTime time = LocalDateTime.now().plusDays(1);// 过期时间 = 当前时间加一天
        return JWT.create()
                .withPayload(playload)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(algorithm);
    }

    // 解密
    public DecodedJWT decode(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (TokenExpiredException | SignatureVerificationException | JWTDecodeException e) {
            if (e instanceof SignatureVerificationException || e instanceof JWTDecodeException) {
                throw XException.builder().code(Code.FORBIDDEN).build();
            }
            throw XException.builder().code(Code.TOKEN_EXPIRED).build();
        }
    }
}
/*
捕获了 3 种常见的 JWT 验证异常，并根据异常类型抛出自定义的 XException：
TokenExpiredException：token 已过期
SignatureVerificationException：签名验证失败（token 可能被篡改，或使用了错误的密钥验证）
JWTDecodeException：token 格式错误（无法被正确解析，如非 JWT 格式的字符串）
 */