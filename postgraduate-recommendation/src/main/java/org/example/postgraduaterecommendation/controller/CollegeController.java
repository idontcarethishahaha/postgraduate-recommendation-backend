package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.*;
import org.example.postgraduaterecommendation.dto.CounselorDTO;
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
//    @PostMapping("users")
//    public ResultVO postCategoryAdmin(@RequestBody RegisterUserDTO registerUser,
//                                      @RequestAttribute(TokenAttribute.CID) long cid) {
//        registerUser.setCollegeId(cid);
//        userService.addCounselor(registerUser);
//        return ResultVO.success();
//    }
    @PostMapping("users")
    public ResultVO postCounselors(@RequestBody CounselorDTO counselorDTO,
                                      @RequestAttribute(TokenAttribute.CID) long cid) {
        counselorDTO.setCollegeId(cid);
        userService.addCounselor(counselorDTO);
        return ResultVO.success();
    }

    //类别管理页面使用，获取类别及专业
    @GetMapping("categories/majors")
    public ResultVO getCategoriesAll(@RequestAttribute(TokenAttribute.CID) long cid) {
        return ResultVO.success(majorCategoryService.listMajorCategoryDTOs(cid));
    }
//=================================================
    //添加类别
    @PostMapping("categories")
    public ResultVO addCategory(@RequestBody MajorCategory majorCategory,
                                @RequestAttribute(TokenAttribute.CID) long cid) {
        majorCategory.setCollegeId(cid);
        majorCategoryService.addMajorCategory(majorCategory);
        return ResultVO.success();
    }

    //移除类别
    @DeleteMapping("categories/{mcid}")
    public ResultVO removeCategory(@PathVariable long mcid){
        try{
            majorCategoryService.removeMajorCategory(mcid);
            return ResultVO.success();
        }catch (Exception e){
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("删除类别失败")
                    .build();
        }
    }
//==================================================
    //添加专业
    @PostMapping("majors")
    public ResultVO addMajor(@RequestBody Major major,
                             @RequestAttribute(TokenAttribute.CID) long cid) {
        // 把从Token拿到的cid赋值给major的collegeId
        major.setCollegeId(cid);
        majorCategoryService.addMajor(major);
        return ResultVO.success();
    }

    //移除专业
    @DeleteMapping("majors/{mid}")
    public ResultVO removeMajor(@PathVariable long mid){
        majorCategoryService.removeMajor(mid);
        return ResultVO.success();
    }


//================================================
    //添加指标项，目前有问题
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
