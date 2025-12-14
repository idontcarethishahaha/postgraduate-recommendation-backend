package org.example.postgraduaterecommendation.controller;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.dto.UserInfoDTO;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.service.UserService;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.example.postgraduaterecommendation.vo.TokenAttribute;
import org.springframework.web.bind.annotation.*;

/**
 * @author wuwenjin
 */
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class CommonController {
    private final UserService userService;

    @GetMapping("info")
    public ResultVO getInfo(@RequestAttribute(TokenAttribute.UID) long uid,
                            @RequestAttribute(TokenAttribute.ROLE) String role) {
        UserInfoDTO userInfoDTO;
        switch (role) {
            case User.COLLEGE_ADMIN:
                userInfoDTO = userService.getCollegeAdminUserInfo(uid);
                break;
            case User.COUNSELOR:
                userInfoDTO = userService.getConselorUserInfo(uid);
                break;
            case User.STUDENT:
                userInfoDTO = userService.getStudentUserInfo(uid);
                break;
            case User.ADMIN:
                userInfoDTO = UserInfoDTO.builder().name("admin").build();
                break;
            default:
                throw XException.builder().code(Code.FORBIDDEN).build();
        }
        return ResultVO.success(userInfoDTO);
    }

//    @PostMapping("passwords")
//    public ResultVO postPassword(@RequestBody User user,
//                                 @RequestAttribute(TokenAttribute.UID) long uid) {
//        userService.updatePassword(uid, user.getPassword());
//        return ResultVO.success();
//    }
}