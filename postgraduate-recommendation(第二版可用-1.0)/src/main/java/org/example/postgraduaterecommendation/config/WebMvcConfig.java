package org.example.postgraduaterecommendation.config;

import lombok.RequiredArgsConstructor;

import org.example.postgraduaterecommendation.interceptor.AdminInterceptor;
import org.example.postgraduaterecommendation.interceptor.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wuwenjin
 */
// 拦截器配置类

@Configuration //标记这是spring配置类
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    //实现 WebMvcConfigurer 接口，重写 addInterceptors 方法来自定义 Spring MVC 的拦截器规则
    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final CollegeInterceptor collegeInterceptor;
    private final StudentInterceptor studentInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**") // 拦截所有以 /api/ 开头的请求
                .excludePathPatterns(
                        "/api/open/login",
                        "/api/open/register",           // 排除注册接口
                        "/api/open/register/**",        // 或者排除所有注册相关
                        "/api/open/colleges",           // 排除学院列表
                        "/api/open/colleges1",           // 排除学院列表
                        //"/api/open/colleges/*/majors"   // 排除专业列表
                        "/api/open/colleges/**"
                );

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");

        registry.addInterceptor(collegeInterceptor)
                .addPathPatterns("/api/college/**");

        registry.addInterceptor(studentInterceptor)
                .addPathPatterns("/api/student/**");
    }
}