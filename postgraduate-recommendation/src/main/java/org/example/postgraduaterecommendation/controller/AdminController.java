package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;

import org.example.postgraduaterecommendation.dox.College;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author wuwenjin
 */
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class AdminController {
    private final CollegeService collegeService;

    //添加学院
    @PostMapping("admin/colleges")
    public ResultVO postCollege(@RequestBody College college) {
        collegeService.addCollege(college);
        return ResultVO.success();
    }

    //查询所有学院
    @GetMapping("admin/colleges")
    public ResultVO getColleges() {
        List<College> colleges = collegeService.listColleges();
        return ResultVO.success(colleges);
    }

    // 删除学院
    @DeleteMapping("admin/colleges/{cid}")
    public ResultVO removeCollege(@PathVariable Long cid) {
        try {
            collegeService.removeCollege(cid);
            return ResultVO.success();
        } catch (Exception e) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("删除学院失败")
                    .build();
        }
    }

    //新增学院管理员
    @PostMapping("admin/users")
    public ResultVO postUser(@RequestBody User user) {
        collegeService.addCollegeAdmin(user);
        return ResultVO.success();
    }
}

