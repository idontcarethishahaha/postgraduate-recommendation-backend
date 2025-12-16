package org.example.postgraduaterecommendation.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.component.JWTComponent;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class LoginController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTComponent jwtComponent;

    @PostMapping("open/login")
    public ResultVO login(@RequestBody User userLogin, HttpServletResponse response) {

        Optional<User> userOptional = userService.getUser(userLogin.getAccount());


        if (userOptional.isPresent()) {
            User u = userOptional.get();
            // 密码校验逻辑
            if (!passwordEncoder.matches(userLogin.getPassword(), u.getPassword())) {
                return ResultVO.error(Code.LOGIN_ERROR);
            }

            String role = u.getRole();
            Map<String, Object> map = new HashMap<>();
            map.put(TokenAttribute.UID, u.getId());
            map.put(TokenAttribute.ROLE, role);
            if (u.getCollegeId() != null) {
                map.put(TokenAttribute.CID, u.getCollegeId());
            }
            if (u.getMajorId() != null) {
                map.put(TokenAttribute.MID, u.getMajorId());
            }
            if (u.getMajorCategoryId() != null) {
                map.put(TokenAttribute.MCID, u.getMajorCategoryId());
            }

            // 设置响应头
            response.addHeader(TokenAttribute.ROLE, u.getRole());
            response.addHeader(TokenAttribute.TOKEN, jwtComponent.encode(map));

            return ResultVO.success(u);
        } else {
            // 无用户时返回登录错误
            return ResultVO.error(Code.LOGIN_ERROR);
        }
    }


        // 生成token
        //String token = jwtComponent.encode(claims);
        // 设置响应头
//        response.setHeader("token", token);
//        response.setHeader("role", role);
        //return ResultVO.success(userR);
}