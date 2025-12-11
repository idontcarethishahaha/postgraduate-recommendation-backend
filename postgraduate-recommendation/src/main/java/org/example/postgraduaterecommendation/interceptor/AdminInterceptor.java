package org.example.postgraduaterecommendation.interceptor;

/*
 * @author wuwenjin
 */
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(User.ADMIN.equals(request.getAttribute("role"))) {
            return true;
        }
        throw XException
                .builder()
                .code(Code.FORBIDDEN)//无权限异常
                .build();
    }
}