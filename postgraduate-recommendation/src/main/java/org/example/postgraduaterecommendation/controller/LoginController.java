package org.example.postgraduaterecommendation.controller;

/*
 * @author wuwenjin
 */

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.component.JWTComponent;
import org.example.postgraduaterecommendation.dox.CounselorInfo;
import org.example.postgraduaterecommendation.dox.StudentInfo;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
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
    public ResultVO login(@RequestBody User user, HttpServletResponse response) {
        // 处理用户查询
        Optional<User> userOpt = userService.getUser(user.getAccount());

        // 验证用户存在且密码正确
        boolean isValidUser = userOpt.map(u -> passwordEncoder.matches(user.getPassword(), u.getPassword()))
                .orElse(false);

        if (!isValidUser) {
            log.warn("登录失败，账号或密码错误: {}", user.getAccount());
            return ResultVO.error(Code.LOGIN_ERROR);
        }

        User userR = userOpt.get();
        String role = userR.getRole();

        Long userId = userR.getId(); // 用户ID（用于查询中间表）

        // 构建token参数map（基于角色动态添加字段）
        Map<String, Object> claims = new HashMap<>();
        // 所有角色都必须包含的基础字段
        claims.put("uid", userR.getId());
        claims.put("role", role);

        // 根据角色查询中间表，添加特有字段
        switch (role) {
            case User.COLLEGE_ADMIN:
                // 学院管理员：从user表获取college_id
                claims.put("cid", userR.getCollegeId());
                break;
            case User.COUNSELOR:
                // 辅导员：从counselor_info中间表获取major_category_id，同时添加college_id
                CounselorInfo counselorInfo = userService.getCounselorInfo(userId)
                        .orElseThrow(() -> XException.builder().number(Code.ERROR).message("辅导员信息不存在").build());
                claims.put("cid", userR.getCollegeId()); // 辅导员的college_id在user表
                claims.put("mcid", counselorInfo.getMajorCategoryId());
                break;
            case User.STUDENT:
                // 学生：从student_info中间表获取major_id，同时添加college_id
                StudentInfo studentInfo = userService.getStudentInfo(userId)
                        .orElseThrow(() -> XException.builder().number(Code.ERROR).message("学生信息不存在").build());
                claims.put("cid", userR.getCollegeId()); // 学生的college_id在user表
                claims.put("mid", studentInfo.getMajorId());
                break;
            case User.ADMIN: // 超级管理员
                // 无需额外字段
                break;
            default:
                throw XException.builder().number(Code.ERROR).message("未知角色").build();
        }

        // 生成token
        String token = jwtComponent.encode(claims);

        // 设置响应头
        response.setHeader("token", token);
        response.setHeader("role", role);

        return ResultVO.success(userR);
    }
}