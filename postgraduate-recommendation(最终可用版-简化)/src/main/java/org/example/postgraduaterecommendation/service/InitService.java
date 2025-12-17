package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.User;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.postgraduaterecommendation.repository.*;
/**
 * @author wuwenjin
 */
/*
当 Spring Boot 应用启动完成后（即 ApplicationReadyEvent 事件触发时），
InitService 中的 init() 方法会被自动调用，用于初始化默认的超级管理员用户。
 */
@Service
@RequiredArgsConstructor
public class InitService {

    //注入持久层组件
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional //事务
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        String account = "admin";
        //UserRepository无需手动写count()，父接口已定义
        long count = userRepository.count();
        if(count > 0){
            return;
        }
        User u = User.builder()
                .name("超级管理员")
                .account("admin")
                .password(passwordEncoder.encode(account))
                .tel(null)
                .role(User.ADMIN)
                .collegeId(null)
                .majorId(null)
                .majorCategoryId(null)
                .build();
        userRepository.save(u);
    }
}

