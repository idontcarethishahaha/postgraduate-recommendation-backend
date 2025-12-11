package org.example.postgraduaterecommendation.controller;
/*
 * @author wuwenjin
 */

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.College;
import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.example.postgraduaterecommendation.dto.MajorAddDTO;
import org.example.postgraduaterecommendation.dto.MajorCategoryAddDTO;
import org.example.postgraduaterecommendation.dto.MajorCategoryUpdateDTO;
import org.example.postgraduaterecommendation.dto.MajorUpdateDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class CollegeController {


    private final CollegeService collegeService;
    private final UserService userService;


    // 学院管理员可以为学院添加类别(从token获取学院ID
    @PostMapping("collegeadmin/categories")
    public ResultVO postMajorCategories(@RequestBody MajorCategoryAddDTO majorCategoryAddDTO,
                                        HttpServletRequest request) {
        // 从request中获取拦截器解析的学院ID
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        //直接传递学院ID到service，不依赖前端传递
        collegeService.addMajorCategory(majorCategoryAddDTO, cid);
        return ResultVO.ok();
    }


    // 获取学院下的所有类别
    @GetMapping("collegeadmin/categories")
    public ResultVO getCategories(HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        List<MajorCategory> categories = collegeService.listAllMajorCategories(cid);

        //将ID转换为String返回给前端,避免精度丢失
        List<Map<String, Object>> result = categories.stream()
                .map(category -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", category.getId().toString()); //转换为String
                    map.put("name", category.getName());
                    map.put("calculationRule", category.getCalculationRule());
                    map.put("createTime", category.getCreateTime());
                    return map;
                })
                .collect(Collectors.toList());

        return ResultVO.success(result);
    }



    // 删除某个类别（增加学院权限验证
    @DeleteMapping("collegeadmin/categories/{mcid}")
    public ResultVO deleteMajorCategory(@PathVariable Long mcid, HttpServletRequest request) {
        // 从token获取学院ID，确保只能删除自己学院的类别
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        collegeService.deleteMajorCategory(mcid, cid);
        return ResultVO.ok();
    }

    // 修改类别
    @PutMapping("collegeadmin/categories/{mcid}")
    public ResultVO updateMajorCategory(@PathVariable Long mcid,
                                        @RequestBody MajorCategoryUpdateDTO updateDTO,
                                        HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        collegeService.updateMajorCategory(mcid, updateDTO, cid);
        return ResultVO.ok();
    }

    // 学院管理员为类别添加专业
    @PostMapping("collegeadmin/majors")
    public ResultVO addMajor(@RequestBody MajorAddDTO majorAddDTO, HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        collegeService.addMajor(majorAddDTO, cid);
        return ResultVO.ok();
    }

    // 查询某类别下的所有专业
    @GetMapping("collegeadmin/categories/{mcid}/majors")
    public ResultVO getMajorsByCategory(@PathVariable Long mcid, HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        return ResultVO.success(collegeService.listMajorsByCategory(mcid, cid));
    }



    // 修改专业信息
    @PutMapping("collegeadmin/majors/{mid}")
    public ResultVO updateMajor(@PathVariable Long mid,
                                @RequestBody MajorUpdateDTO updateDTO,
                                HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        collegeService.updateMajor(mid, updateDTO, cid);
        return ResultVO.ok();
    }

    // 删除专业
    @DeleteMapping("collegeadmin/majors/{mid}")
    public ResultVO removeMajor(@PathVariable Long mid, HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        collegeService.removeMajor(mid, cid);
        return ResultVO.ok();
    }

    // 查询学院下的所有专业
    @GetMapping("collegeadmin/majors")
    public ResultVO getAllMajors(HttpServletRequest request) {
        Long cid = (Long) request.getAttribute("cid");
        if (cid == null) {
            throw XException.builder().code(Code.FORBIDDEN).build();
        }

        return ResultVO.success(collegeService.listAllMajors(cid));
    }


    @GetMapping("user/majors")
    public ResultVO getMajors(@RequestParam(required = false) Long collegeId,
                              @RequestParam(required = false) Long majorCategoryId,
                              HttpServletRequest request) {
        // 从request获取token中的简写，但使用驼峰命名变量
        Long userId = (Long) request.getAttribute("uid");        // token中是"uid"
        String role = (String) request.getAttribute("role");
        Long collegeIdFromToken = (Long) request.getAttribute("cid");  // token中是"cid"

        return ResultVO.success(collegeService.listMajorsByRole(
                collegeId, majorCategoryId, userId, role, collegeIdFromToken
        ));
    }

    //-----------------------------------------------------------------------------------------

    @GetMapping("open/colleges")
    public ResultVO getAllColleges() {
        List<College> colleges = collegeService.listAllColleges();

        //转换ID为String
        List<Map<String, Object>> result = colleges.stream()
                .map(college -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", college.getId().toString());
                    map.put("name", college.getName());
                    return map;
                })
                .collect(Collectors.toList());

        return ResultVO.success(result);
    }
    // 根据学院获取专业（开放接口）
    @GetMapping("open/colleges/{collegeId}/majors")
    public ResultVO getMajorsByCollege(@PathVariable Long collegeId) {
        return ResultVO.success(collegeService.listMajorsByCollegeId(collegeId));
    }

//----------------------------------------------------------------------------------

    // 根据id查询单个学院
    @GetMapping("open/colleges/{collegeId}")
    public ResultVO getCollegeById(@PathVariable Long collegeId) {
        College college = collegeService.getCollegeById(collegeId);
        Map<String, Object> result = new HashMap<>();
        result.put("id", college.getId().toString()); // ID转String避免前端精度丢失
        result.put("name", college.getName());

        return ResultVO.success(result);
    }


}
