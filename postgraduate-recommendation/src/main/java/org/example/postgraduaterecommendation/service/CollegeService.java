package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.College;
import org.example.postgraduaterecommendation.dox.Major;
import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.dto.AdminDO;
import org.example.postgraduaterecommendation.dto.AdminResp;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.repository.CollegeRepository;
import org.example.postgraduaterecommendation.repository.MajorRepository;
import org.example.postgraduaterecommendation.repository.UserCategoryRepository;
import org.example.postgraduaterecommendation.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CollegeService {
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCategoryRepository userCategoryRepository;

    //添加学院
    @Transactional
    public void addCollege(College college) {
        collegeRepository.save(college);
    }

    //查询所有学院列表
    public List<College> listColleges() {
        return collegeRepository.findAll();
    }

    // 根据学院获取专业
    public List<Major> listMajorsByCollegeId(Long cid) {
        return majorRepository.findByCollegeId(cid);
    }
    // 删除学院
    @Transactional
    public void removeCollege(Long cid) {
        //existsById()是CrudRepository接口自带的方法
        if (!collegeRepository.existsById(cid)) {
            throw XException.builder()
                    .codeNum(Code.ERROR)
                    .message("学院不存在")
                    .build();
        }
        collegeRepository.deleteById(cid);
    }

    //添加学院管理员
    @Transactional
    public void addCollegeAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getAccount()));
        user.setRole(User.COLLEGE_ADMIN);
        userRepository.save(user);
    }

    //查询管理员列表
    public List<AdminResp> listAdmins(long cid, String role) {
        List<AdminDO> admins = userCategoryRepository.findByCollegeId(cid, role);

        // 按 majorCategoryId分组
        Map<Long, List<AdminDO>> groupByCatId = admins.stream()
                .collect(Collectors.groupingBy(AdminDO::getMajorCategoryId));

        // 转换为AdminResp列表
        return groupByCatId.values().stream()
                .map(adminDOS -> {
                    AdminDO first = adminDOS.getFirst();
                    MajorCategory majorCategory = MajorCategory.builder()
                            .id(first.getMajorCategoryId())
                            .name(first.getMajorCategoryName())
                            .build();
                    List<User> list = adminDOS.stream()
                            .map(adminDO -> User.builder()
                                    .id(adminDO.getUserId())
                                    .name(adminDO.getUserName())
                                    .build())
                            .toList();
                    return AdminResp.builder()
                            .users(list)
                            .majorCategory(majorCategory)
                            .build();
                }).toList();
    }
}