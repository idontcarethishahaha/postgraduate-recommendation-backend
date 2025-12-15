package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.dox.UserCategory;
import org.example.postgraduaterecommendation.dto.RegisterUserDTO;
import org.example.postgraduaterecommendation.dto.UserInfoDTO;
import org.example.postgraduaterecommendation.repository.UserCategoryRepository;
import org.example.postgraduaterecommendation.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCategoryRepository userCategoryRepository;

    //根据账号查询用户
    public Optional<User> getUser(String account) {
        return userRepository.findByAccount(account);
    }

    //查询学院管理员用户信息
    public Optional<UserInfoDTO> getCollegeAdminUserInfo(long id) {
        return userRepository.findCollegeAdminUserInfo(id);
    }

    //查询分类管理员用户信息
    public Optional<UserInfoDTO> getCounselorUserInfo(long id) {
        return userRepository.findCounselorUserInfo(id);
    }

    //查询学生用户信息
    public Optional<UserInfoDTO> getStudentUserInfo(long id) {
        return userRepository.findStudentUserInfo(id);
    }

    //更新密码
    @Transactional
    public void updatePassword(long uid, String password) {
        userRepository.updatePassword(uid, passwordEncoder.encode(password));
    }

    //添加辅导员
    @Transactional
    public void addCounselor(RegisterUserDTO registerUser) {
        User newUser = new User();
        BeanUtils.copyProperties(registerUser, newUser);
        newUser.setRole(User.COUNSELOR);
        newUser.setPassword(passwordEncoder.encode(registerUser.getAccount()));

        // 保存用户
        User savedUser = userRepository.save(newUser);

        // 遍历分类ID，批量保存用户-分类关联
        List<Long> majorCategoryIds = registerUser.getMajorCategoryIds();
        for (Long majorCategoryId : majorCategoryIds) {
            UserCategory userCategory = UserCategory.builder()
                    .userId(savedUser.getId())
                    .majorCategoryId(majorCategoryId)
                    .build();
            userCategoryRepository.save(userCategory);
        }
    }

    //添加学生
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(RegisterUserDTO registerUser) {
        if (registerUser.getMajorCategoryId() == null) {
            throw new IllegalArgumentException("专业类别ID（majorCategoryId）不能为空");
        }

        User newUser = new User();
        BeanUtils.copyProperties(registerUser, newUser);
        newUser.setRole(User.STUDENT);
        newUser.setPassword(passwordEncoder.encode(registerUser.getAccount()));

        // 保存用户
        User savedUser = userRepository.save(newUser);

        // 保存用户-分类关联
        UserCategory userCategory = UserCategory.builder()
                .userId(savedUser.getId())
                .majorCategoryId(registerUser.getMajorCategoryId())
                .build();
        userCategoryRepository.save(userCategory);
    }


    //重置密码
    @Transactional
    public Integer resetPassword(long cid, String account) {//cid 学院id
        // 返回受影响行数
        return userRepository.updatePassword(cid, account, passwordEncoder.encode(account));
    }


}