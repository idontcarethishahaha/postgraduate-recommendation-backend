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
    //@Value("${my.secretkey}")
    private String secretkey;
    private Algorithm algorithm;

    // 组件初始化后，初始化加密算法对象。无需反复创建
    @PostConstruct
    private void init() {
        algorithm = Algorithm.HMAC256(secretkey);
    }

    public String encode(Map<String, Object> map) {
        // 1ds过期
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        return encodePos(JWT.create()
                .withPayload(map)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(algorithm));
    }

    public DecodedJWT decode(String token) {
        try {
            return JWT.require(algorithm).build().verify(decodePos(token));
        } catch (TokenExpiredException | SignatureVerificationException | JWTDecodeException e) {
            if (e instanceof SignatureVerificationException || e instanceof JWTDecodeException) {
                throw XException.builder().code(Code.FORBIDDEN).build();
            }
            throw XException.builder().code(Code.TOKEN_EXPIRED).build();
        }
    }

    private final int POS = 37;

    private String encodePos(String str) {
        return new StringBuilder(str).insert(POS, "W").toString();
    }

    private String decodePos(String str) {
        return new StringBuilder(str).deleteCharAt(POS).toString();
    }
}