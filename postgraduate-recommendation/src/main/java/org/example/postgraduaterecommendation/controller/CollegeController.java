package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.*;
import org.example.postgraduaterecommendation.dto.RegisterUserDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.CollegeService;
import org.example.postgraduaterecommendation.service.ItemService;
import org.example.postgraduaterecommendation.service.MajorCategoryService;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuwenjin
 * 学院管理员
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/college")
public class CollegeController {
    private final MajorCategoryService majorCategoryService;
    private final UserService userService;
     private final ItemService itemService;
    private final CollegeService collegeService;

    //查询辅导员列表
    @GetMapping("categories/users")
    public ResultVO getUsers(@RequestAttribute(TokenAttribute.CID) long cid) {
        return ResultVO.success(collegeService.listAdmins(cid, User.COUNSELOR));
    }

    //添加辅导员
    @PostMapping("users")
    public ResultVO postCategoryAdmin(@RequestBody RegisterUserDTO registerUser,
                                      @RequestAttribute(TokenAttribute.CID) long cid) {
        registerUser.setCollegeId(cid);
        userService.addCounselor(registerUser);
        return ResultVO.success();
    }

    //获取全部类别及专业
    @GetMapping("categories/majors")
    public ResultVO getCategoriesAll(@RequestAttribute(TokenAttribute.CID) long cid) {
        return ResultVO.success(majorCategoryService.listMajorCategoryDTOs(cid));
    }

    //添加类别
    @PostMapping("categories")
    public ResultVO addCategory(@RequestBody MajorCategory majorCategory,
                                @RequestAttribute(TokenAttribute.CID) long cid) {
        majorCategory.setCollegeId(cid);
        majorCategoryService.addMajorCategory(majorCategory);
        return ResultVO.success();
    }

    //添加专业
    @PostMapping("majors")
    public ResultVO addMajor(@RequestBody Major major,
                             @RequestAttribute(TokenAttribute.CID) long cid) {
        majorCategoryService.addMajor(major);
        return ResultVO.success();
    }

    //添加指标项
    @PostMapping("items")
    public ResultVO addItem(@RequestBody Item item,
                            @RequestAttribute(TokenAttribute.CID) long cid) {
        itemService.addItem(item);
        return ResultVO.success();
    }

    //重置密码
    @PutMapping("passwords/{account}")
    public ResultVO putPassword(@PathVariable String account,
                                @RequestAttribute(TokenAttribute.CID) long cid) {
        // 调用重置密码，返回受影响行数
        Integer affectedRows = userService.resetPassword(cid, account);

        if (affectedRows == null || affectedRows <= 0) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("错误，账号: " + account + "，不存在")
                    .build();
        }
        return ResultVO.success();
    }


}
