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
        String token = request.getHeader("token");
        if (token == null) {
            throw XException.builder().code(Code.UNAUTHORIZED).build(); // 未登录异常
        }
        // token 解密
        DecodedJWT decode = jwtComponent.decode(token);
        //String uid = decode.getClaim("uid").asString();
        Long uid = decode.getClaim("uid").asLong();
        String role = decode.getClaim("role").asString();
        request.setAttribute("uid", uid);
        request.setAttribute("role", role);

        // 解析并存储学院id
        // 先判断Token中是否包含college_id，避免解析空值
        if (!decode.getClaim("cid").isMissing()) {
            Long cid = decode.getClaim("cid").asLong();
            request.setAttribute("cid", cid); // 存入request，供控制器获取
        }

        return true;

    }
}