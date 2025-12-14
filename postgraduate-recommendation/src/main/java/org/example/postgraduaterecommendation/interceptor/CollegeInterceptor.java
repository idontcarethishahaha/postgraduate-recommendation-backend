package org.example.postgraduaterecommendation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author wuwenjin
 */
@Component
public class CollegeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(User.COLLEGE_ADMIN.equals(request.getAttribute(TokenAttribute.ROLE))||User.COUNSELOR.equals(request.getAttribute(TokenAttribute.ROLE))) {
            return true;
        }
        throw XException
                .builder()
                .code(Code.FORBIDDEN)//无权限异常
                .build();
    }
}
