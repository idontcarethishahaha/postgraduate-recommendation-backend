package org.example.postgraduaterecommendation.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.College;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.dto.CollegeAddDTO;
import org.example.postgraduaterecommendation.dto.CollegeUpdateDTO;
import org.example.postgraduaterecommendation.dto.CounselorAddDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.CounselorVO;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;


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
    private final UserService userService;
    private final CollegeService collegeService;

    // 重置用户密码
    @PutMapping("admin/users/{account}/password")
    public ResultVO putPassword(@PathVariable String account) {
        userService.updateUserPassword(account);
        return ResultVO.ok();
    }

    //---------------------------------------------------------------------------------------------------

    // 学院管理
    // 获取所有学院
    @GetMapping("admin/colleges")
    public ResultVO getColleges() {
        List<College> colleges = collegeService.listAllColleges();

        // 转换ID为String返回给前端
        List<Map<String, Object>> result = colleges.stream()
                .map(college -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", college.getId().toString());
                    map.put("name", college.getName());
                    map.put("createTime", college.getCreateTime());
                    map.put("updateTime", college.getUpdateTime());
                    return map;
                })
                .collect(Collectors.toList());

        return ResultVO.success(result);
    }

    // 添加学院
    @PostMapping("admin/colleges")
    public ResultVO postCollege(@RequestBody CollegeAddDTO collegeAddDTO) {
        collegeService.addCollege(collegeAddDTO);
        return ResultVO.ok();
    }

    // 编辑学院信息
    @PutMapping("admin/colleges/{collegeId}")
    public ResultVO updateCollege(@PathVariable String collegeId,
                                  @RequestBody CollegeUpdateDTO updateDTO) {
        try {
            Long id = Long.parseLong(collegeId);
            collegeService.updateCollege(id, updateDTO);
            return ResultVO.ok();
        } catch (NumberFormatException e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("学院ID格式错误")
                    .build();
        }
    }

    // 删除学院
    @DeleteMapping("admin/colleges/{collegeId}")
    public ResultVO removeCollege(@PathVariable String collegeId) {
        try {
            Long id = Long.parseLong(collegeId);
            collegeService.deleteCollege(id);
            return ResultVO.ok();
        } catch (NumberFormatException e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("学院ID格式错误")
                    .build();
        }
    }

    // 获取学院管理员列表
    @GetMapping("admin/colleges/{collegeId}/collegeadmins")
    public ResultVO getCollegeAdmins(@PathVariable String collegeId) {
        try {
            Long id = Long.parseLong(collegeId);
            List<User> admins = userService.getCollegeAdmins(id);

            // 转换用户ID为String
            List<Map<String, Object>> result = admins.stream()
                    .map(user -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", user.getId().toString());
                        map.put("name", user.getName());
                        map.put("account", user.getAccount());
                        map.put("tel", user.getTel());
                        map.put("createTime", user.getCreateTime());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResultVO.success(result);
        } catch (NumberFormatException e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("学院ID格式错误")
                    .build();
        }
    }

    // 移除学院管理员
    @DeleteMapping("admin/colleges/{collegeId}/collegeadmins/{userId}")
    public ResultVO removeCollegeAdmin(@PathVariable String collegeId,
                                       @PathVariable String userId) {
        try {
            Long collegeIdLong = Long.parseLong(collegeId);
            Long userIdLong = Long.parseLong(userId);
            userService.removeCollegeAdmin(collegeIdLong, userIdLong);
            return ResultVO.ok();
        } catch (NumberFormatException e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("ID格式错误")
                    .build();
        }
    }

    // 添加学院级管理员
    @PostMapping("admin/colleges/{collegeId}/collegeadmins")
    public ResultVO addCollegeAdmin(@PathVariable String collegeId,
                                    @RequestBody Map<String, Object> userData) {
        try {
            Long collegeIdLong = Long.parseLong(collegeId);

            User user = User.builder()
                    .name((String) userData.get("name"))
                    .account((String) userData.get("account"))
                    .tel((String) userData.get("tel"))
                    .password((String) userData.get("password"))
                    .build();

            userService.addCollegeAdmin(collegeIdLong, user);
            return ResultVO.ok();
        } catch (NumberFormatException e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("学院ID格式错误")
                    .build();
        }
    }


    // 下方为学院管理员功能---------------------------------------------------------------------------

    // 获取所有辅导员
    @GetMapping("collegeadmin/counselors")
    public ResultVO getCounselors(HttpServletRequest request) {
        Long collegeId = (Long) request.getAttribute("cid");
        if (collegeId == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }
        List<CounselorVO> counselors = userService.getCounselorsWithCategory(collegeId);
        return ResultVO.success(counselors);
    }


    // 为特定类别添加辅导员
    @PostMapping("collegeadmin/counselors")// HttpServletRequest request,这个参数不要了
    public ResultVO addCounselor(@RequestBody CounselorAddDTO counselorAddDTO,@RequestAttribute("cid") Long cid) {
        //Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        // 调用Service层添加辅导员
        CounselorVO counselor = userService.addCounselor(cid, counselorAddDTO);

        // 将ID转换为String返回给前端
        Map<String, Object> result = new HashMap<>();
        result.put("id", counselor.getId().toString());
        result.put("name", counselor.getName());
        result.put("account", counselor.getAccount());
        result.put("tel", counselor.getTel());
        result.put("majorCategoryId", counselor.getMajorCategoryId().toString());
        result.put("majorCategoryName", counselor.getMajorCategoryName());
        result.put("createTime", counselor.getCreateTime());

        return ResultVO.success(result);
    }

    // 删除辅导员
    @DeleteMapping("collegeadmin/counselors/{counselorId}")
    public ResultVO removeCounselor(@PathVariable String counselorId,
                                    HttpServletRequest request) {
        try {
            Long cid = (Long) request.getAttribute("cid");
            if (cid == null) {
                throw XException.builder().code(Code.FORBIDDEN).build();
            }

            //直接传递String类型的ID
            userService.removeCounselor(counselorId, cid);
            return ResultVO.ok();
        } catch (Exception e) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("删除辅导员失败: " + e.getMessage())
                    .build();
        }
    }
}

