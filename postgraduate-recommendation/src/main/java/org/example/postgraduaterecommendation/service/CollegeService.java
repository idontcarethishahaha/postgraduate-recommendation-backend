package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.*;
import org.example.postgraduaterecommendation.dto.*;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.postgraduaterecommendation.repository.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeService {
    private final CollegeRepository collegeRepository;
    private final MajorCategoryRepository majorCategoryRepository;
    private final MajorRepository majorRepository;
    private final CounselorInfoRepository counselorInfoRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // 添加学院
    @Transactional
    public void addCollege(CollegeAddDTO collegeAddDTO) {
        boolean nameExists = collegeRepository.existsByName(collegeAddDTO.getName());
        if (nameExists) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("学院名称已存在")
                    .build();
        }

        College college = College.builder()
                .name(collegeAddDTO.getName())
                .build();
        collegeRepository.save(college);
    }

    // 编辑学院信息
    @Transactional
    public void updateCollege(Long id, CollegeUpdateDTO updateDTO) {
        College college = collegeRepository.findById(id)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("学院不存在")
                        .build());

        // 检查名称是否重复（排除自身）
        if (!college.getName().equals(updateDTO.getName())) {
            boolean nameExists = collegeRepository.existsByName(updateDTO.getName());
            if (nameExists) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("学院名称已存在")
                        .build();
            }
            college.setName(updateDTO.getName());
            collegeRepository.save(college);
        }
    }



    // 删除学院
    @Transactional
    public void deleteCollege(Long id) {
        if (!collegeRepository.existsById(id)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("学院不存在")
                    .build();
        }
        collegeRepository.deleteById(id);
    }



    // 为学院添加类别,从token获取学院ID
    @Transactional
    public void addMajorCategory(MajorCategoryAddDTO majorCategoryAddDTO, Long collegeId) {
        // 学院是否存在（使用从token获取的collegeId）
        collegeRepository.findById(collegeId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("学院不存在，无法添加专业类别")
                        .build());

        // 该学院下是否已存在同名类别
        boolean nameExists = majorCategoryRepository.existsByCollegeIdAndName(
                collegeId,  // 使用从token获取的collegeId
                majorCategoryAddDTO.getName()
        );
        if (nameExists) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该学院下已存在同名类别")
                    .build();
        }

        MajorCategory majorCategory = MajorCategory.builder()
                .collegeId(collegeId)  // 使用从token获取的collegeId
                .name(majorCategoryAddDTO.getName())
                .calculationRule(majorCategoryAddDTO.getCalculationRule())
                .build();
        majorCategoryRepository.save(majorCategory);
    }



    public List<MajorCategory> listAllMajorCategories(Long cid) {
        return majorCategoryRepository.findByCollegeId(cid);
    }



    // 删除学院下的某个类别（增加学院权限验证
    @Transactional
    public void deleteMajorCategory(Long mcid, Long collegeId) {
        //类别是否存在且属于当前学院
        MajorCategory category = majorCategoryRepository.findById(mcid)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("类别不存在")
                        .build());

        //类别是否属于当前学院
        if (!category.getCollegeId().equals(collegeId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN) //无权限
                    .build();
        }
        majorCategoryRepository.deleteById(mcid);
    }

    // 修改类别信息
    @Transactional
    public void updateMajorCategory(Long mcid, MajorCategoryUpdateDTO updateDTO, Long collegeId) {
        // 查询类别是否存在且属于当前学院
        MajorCategory category = majorCategoryRepository.findById(mcid)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("类别不存在")
                        .build());

        // 验证类别是否属于当前学院
        if (!category.getCollegeId().equals(collegeId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }

        // 检查名称是否重复（排除自身）
        if (updateDTO.getName() != null && !updateDTO.getName().equals(category.getName())) {
            boolean nameExists = majorCategoryRepository.existsByCollegeIdAndName(
                    collegeId, updateDTO.getName());
            if (nameExists) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("该学院下已存在同名类别")
                        .build();
            }
            category.setName(updateDTO.getName());
        }

        // 更新计算规则
        if (updateDTO.getCalculationRule() != null) {
            category.setCalculationRule(updateDTO.getCalculationRule());
        }

        majorCategoryRepository.save(category);
    }

    @Transactional
    public void addMajor(MajorAddDTO majorAddDTO, Long collegeId) {
        // 验证类别是否存在且属于当前学院
        MajorCategory category = majorCategoryRepository.findById(majorAddDTO.getMajorCategoryId())
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("专业类别不存在")
                        .build());

        // 验证类别是否属于当前学院
        if (!category.getCollegeId().equals(collegeId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }

        // 检查同一类别下是否已存在同名专业
        boolean nameExists = majorRepository.existsByMajorCategoryIdAndName(
                majorAddDTO.getMajorCategoryId(),
                majorAddDTO.getName()
        );
        if (nameExists) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别下已存在同名专业")
                    .build();
        }

        Major major = Major.builder()
                .name(majorAddDTO.getName())
                .majorCategoryId(majorAddDTO.getMajorCategoryId())
                .collegeId(collegeId) // 设置学院ID
                .build();

        majorRepository.save(major);
    }

    // 查询某类别下的所有专业
    public List<Major> listMajorsByCategory(Long majorCategoryId, Long collegeId) {
        // 先验证类别是否属于当前学院
        MajorCategory category = majorCategoryRepository.findById(majorCategoryId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("专业类别不存在")
                        .build());

        if (!category.getCollegeId().equals(collegeId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }

        return majorRepository.findByMajorCategoryId(majorCategoryId);
    }

    // 查询学院下的所有专业
    public List<Major> listAllMajors(Long collegeId) {
        return majorRepository.findByCollegeId(collegeId);
    }

    // 修改专业信息
    @Transactional
    public void updateMajor(Long majorId, MajorUpdateDTO updateDTO, Long collegeId) {
        // 查询专业是否存在且属于当前学院
        Major major = majorRepository.findByIdAndCollegeId(majorId, collegeId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("专业不存在或无权操作")
                        .build());

        // 检查名称是否重复（排除自身）
        if (updateDTO.getName() != null && !updateDTO.getName().equals(major.getName())) {
            boolean nameExists = majorRepository.existsByMajorCategoryIdAndName(
                    major.getMajorCategoryId(), updateDTO.getName());
            if (nameExists) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("该类别下已存在同名专业")
                        .build();
            }
            major.setName(updateDTO.getName());
        }

        majorRepository.save(major);
    }

    //删除专业
    @Transactional
    public void removeMajor(Long majorId, Long collegeId) {
        //专业是否存在且属于当前学院
        Major major = majorRepository.findByIdAndCollegeId(majorId, collegeId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("专业不存在或无权操作")
                        .build());

        majorRepository.deleteById(majorId);
    }



    public List<Major> listMajorsByRole(Long collegeId, Long majorCategoryId,
                                        Long userId, String role, Long collegeIdFromToken) {
        switch (role) {
            case User.STUDENT:
                return listAllMajorsForStudent();
            case User.COUNSELOR:
                return listMajorsForCounselor(userId);
            case User.COLLEGE_ADMIN:
                return listMajorsForCollegeAdmin(collegeId, majorCategoryId, collegeIdFromToken);
            case User.ADMIN:
                return listMajorsForAdmin(collegeId, majorCategoryId);
            default:
                return Collections.emptyList();
        }
    }



    //学生，查看所有专业
    private List<Major> listAllMajorsForStudent() {
        List<Major> majors = new ArrayList<>();
        majorRepository.findAll().forEach(majors::add);
        return majors;
    }

    //辅导员，查看自己管理的类别下的专业
    private List<Major> listMajorsForCounselor(Long counselorId) {
        // 获取辅导员管理的类别ID
        CounselorInfo counselorInfo = counselorInfoRepository.findByUserId(counselorId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("辅导员信息不存在")
                        .build());

        return majorRepository.findByMajorCategoryId(counselorInfo.getMajorCategoryId());
    }

    //学院管理员，查看自己学院的专业
    private List<Major> listMajorsForCollegeAdmin(Long collegeId, Long majorCategoryId, Long userCollegeId) {
        //只能查看自己学院的数据
        if (collegeId != null && !collegeId.equals(userCollegeId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("无权查看其他学院数据")
                    .build();
        }

        if (majorCategoryId != null) {
            MajorCategory category = majorCategoryRepository.findById(majorCategoryId)
                    .orElseThrow(() -> XException.builder()
                            .number(Code.ERROR)
                            .message("类别不存在")
                            .build());

            if (!category.getCollegeId().equals(userCollegeId)) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("无权限")
                        .build();
            }
            return majorRepository.findByMajorCategoryId(majorCategoryId);
        } else {
            return majorRepository.findByCollegeId(userCollegeId);
        }
    }

    //超级管理员，查看所有专业
    private List<Major> listMajorsForAdmin(Long collegeId, Long majorCategoryId) {
        if (collegeId != null && majorCategoryId != null) {
            // 按学院和类别过滤
            return majorRepository.findByCollegeIdAndMajorCategoryId(collegeId, majorCategoryId);
        } else if (collegeId != null) {
            // 按学院过滤
            return majorRepository.findByCollegeId(collegeId);
        } else if (majorCategoryId != null) {
            // 按类别过滤
            return majorRepository.findByMajorCategoryId(majorCategoryId);
        } else {
            List<Major> majors = new ArrayList<>();
            majorRepository.findAll().forEach(majors::add);
            return majors;
        }
    }

    //--------------------------------------------------------------------------------------------------------
//注册学生




    // 获取所有学院
    public List<College> listAllColleges() {
        return collegeRepository.findAll(); // ListCrudRepository直接返回List
    }

    // 根据学院获取专业
    public List<Major> listMajorsByCollegeId(Long collegeId) {
        return majorRepository.findByCollegeId(collegeId);
    }

    public College getCollegeById(Long collegeId) {
        return collegeRepository.findById(collegeId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("学院不存在")
                        .build());
    }
}