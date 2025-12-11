package org.example.postgraduaterecommendation.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dto.StuScoreDTO;
import org.example.postgraduaterecommendation.service.StudentService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.example.postgraduaterecommendation.vo.StuScoreVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;


    //提交成绩
    @PostMapping("score")
    public ResultVO submitScore(
            @RequestBody StuScoreDTO dto,
            HttpServletRequest request
    ) {
        //从请求中获取拦截器解析的学生ID（uid）
        Long userId = (Long) request.getAttribute("uid");
        StuScoreVO result = studentService.submitScore(dto, userId);
        return ResultVO.ok();
    }

    //修改成绩（仅待审核状态可修改）
    @PutMapping("score")
    public ResultVO updateScore(
            @RequestBody StuScoreDTO dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("uid");
        StuScoreVO result = studentService.updateScore(dto, userId);
        return ResultVO.ok();
    }


    //查询个人成绩
    @GetMapping("score")
    public ResultVO getScore(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("uid");
        StuScoreVO scoreVO = studentService.getStudentScore(userId);
        return ResultVO.success(scoreVO);
    }
}