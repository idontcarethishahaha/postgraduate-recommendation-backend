package org.example.postgraduaterecommendation.interceptor;

/*
 * @author wuwenjin
 */
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.component.JWTComponent;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    // 注入token的组件
    private final JWTComponent jwtComponent;

    // ctrl + i
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(TokenAttribute.TOKEN);
        if (token == null) {
            throw XException.builder().code(Code.UNAUTHORIZED).build(); // 未登录异常
        }
        // token 解密
        DecodedJWT decode = jwtComponent.decode(token);
        Long uid = decode.getClaim(TokenAttribute.UID).asLong();
        String role = decode.getClaim(TokenAttribute.ROLE).asString();
        request.setAttribute(TokenAttribute.UID, uid);
        request.setAttribute(TokenAttribute.ROLE, role);

        // 解析并存储
        // 先判断Token中是否包含college_id，避免解析空值
        if (!decode.getClaim(TokenAttribute.CID).isMissing()) {
            Long cid = decode.getClaim(TokenAttribute.CID).asLong();
            request.setAttribute(TokenAttribute.CID,cid); // 存入request，供控制器获取
        }

        if (!decode.getClaim(TokenAttribute.MCID).isMissing()) {
            Long mcid = decode.getClaim(TokenAttribute.MCID).asLong();
            request.setAttribute(TokenAttribute.MCID, mcid); // 存入request，供控制器获取
        }

        if (!decode.getClaim(TokenAttribute.MID).isMissing()) {
            Long mid = decode.getClaim(TokenAttribute.MID).asLong();
            request.setAttribute(TokenAttribute.MID, mid); // 存入request，供控制器获取
        }

        return true;

    }
}