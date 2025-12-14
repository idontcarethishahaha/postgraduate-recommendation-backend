package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;

import org.example.postgraduaterecommendation.dox.College;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    //移除学院管理员
    @DeleteMapping("admin/users/{uid}")
    public ResultVO removeCollegeAdmin(@PathVariable Long uid) {
        try {
            collegeService.removeCollegeAdmin(uid);
            return ResultVO.success();
        } catch (Exception e) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("移除学院管理员失败：" + e.getMessage())
                    .build();
        }
    }

    //获取学院管理员列表
    @GetMapping("admin/users/{cid}")
    public ResultVO getCollegeAdmins(@PathVariable Long cid) {
        try {
            List<User> collegeAdmins = collegeService.getCollegeAdminsByCollegeId(cid);
            if (CollectionUtils.isEmpty(collegeAdmins)) {
                return ResultVO.success(Collections.emptyList());
            }
            List<Map<String, Object>> result = collegeAdmins.stream()
                    .map(user -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", user.getId().toString());
                        map.put("name", user.getName());
                        map.put("account", user.getAccount());
                        map.put("tel", user.getTel());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResultVO.success(result);
        } catch (XException e) {
            throw e;
        } catch (Exception e) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("查询学院管理员列表失败：" + e.getMessage())
                    .build();
        }
    }
}

