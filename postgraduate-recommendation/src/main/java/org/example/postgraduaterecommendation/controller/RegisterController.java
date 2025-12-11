package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dto.StudentRegisterDTO;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author wuwenjin
 */
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;
    // 学生注册接口（开放接口，不需要认证）
    @PostMapping("open/register")
    public ResultVO registerStudent(@RequestBody StudentRegisterDTO registerDTO) {
        userService.registerStudent(registerDTO);
        return ResultVO.ok();
    }
}

