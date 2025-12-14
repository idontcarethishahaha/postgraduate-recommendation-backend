package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.College;
import org.example.postgraduaterecommendation.dto.RegisterUserDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.service.MajorCategoryService;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wuwenjin
 */
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;
    private final CollegeService collegeService;
    private final MajorCategoryService majorCategoryService;


    @PostMapping("open/register")
    public ResultVO register(@RequestBody RegisterUserDTO user) {
        Optional<?> existingUser = userService.getUser(user.getAccount());

        if (existingUser.isPresent()) {
            return ResultVO.error(Code.ERROR, "学号已存在");
        } else {
            userService.addStudent(user);
            return ResultVO.success();
        }
    }

//    // 查看所有学院
//    @GetMapping("open/colleges")
//    public ResultVO getColleges() {
//        List<College> colleges = collegeService.listColleges();
//
//        List<Map<String, Object>> result = colleges.stream()
//                .map(college -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("id", college.getId().toString());
//                    map.put("name", college.getName());
//                    return map;
//                })
//                .collect(Collectors.toList());
//        return ResultVO.success(result);
//    }
//
//    // 根据学院获取专业（开放接口）
//    @GetMapping("open/colleges/{cid}/majors")
//    public ResultVO getMajorsByCollege(@PathVariable Long cid) {
//        return ResultVO.success(collegeService.listMajorsByCollegeId(cid));
//    }
    //====================================================================
    // 查看所有学院
    @GetMapping("open/colleges")
    public ResultVO getColleges() {
        Object collegesAndMajors = majorCategoryService.listCollegesAndMajors();
        return ResultVO.success(collegesAndMajors);
    }
}

