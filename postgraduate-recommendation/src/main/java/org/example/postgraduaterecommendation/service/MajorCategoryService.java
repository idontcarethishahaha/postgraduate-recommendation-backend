package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.Major;
import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.example.postgraduaterecommendation.dto.CollegeDTO;
import org.example.postgraduaterecommendation.dto.MajorCategoryDTO;
import org.example.postgraduaterecommendation.repository.CollegeRepository;
import org.example.postgraduaterecommendation.repository.MajorCategoryRepository;
import org.example.postgraduaterecommendation.repository.MajorRepository;
import org.example.postgraduaterecommendation.repository.UserCategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  CategoryService  DONE
 */
@Service
@RequiredArgsConstructor
public class MajorCategoryService {
    private final MajorCategoryRepository majorCategoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;

    @CacheEvict(value = "majorCategory", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void addMajorCategory(MajorCategory majorCategory) {
        majorCategoryRepository.save(majorCategory);
    }

    @Cacheable(value = "majorCategory", key = "#mcid")
    public MajorCategoryDTO getMajorCategoryAndMajors(long mcid) {
        Optional<MajorCategory> catOpt = majorCategoryRepository.findById(mcid);
        List<Major> majors = majorRepository.findByMajorCategoryId(mcid);

        // 空值处理
        MajorCategory majorCategory = catOpt.orElseThrow(() -> new RuntimeException("类别不存在"));

        return MajorCategoryDTO.builder()
                .majorCategory(majorCategory)
                .majors(majors)
                .build();
    }

    public List<MajorCategoryDTO> listMajorCategoryDTOs(long cid) {
        List<Long> mcids = majorCategoryRepository.findMajorCategoryIdsByCollegeId(cid);
        return listByMajorCategoryIds(mcids);
    }

    public List<MajorCategory> listMajorCategoriesByUserId(long uid) {
        return majorCategoryRepository.findByUserId(uid);
    }

    public List<MajorCategory> listMajorCategoriesByCollegeId(long cid) {
        return majorCategoryRepository.findByCollegeId(cid);
    }

    private List<MajorCategoryDTO> listByMajorCategoryIds(List<Long> mcids) {
        return mcids.stream()
                .map(this::getMajorCategoryAndMajors) // 调用上面同步的getCategoryAndMajors
                .collect(Collectors.toList());
    }

//    public List<CollegeDTO> listCollegesAndMajors() {
//        List<?> colleges = collegeRepository.findAll();
//
//        return ((List<?>) colleges).stream()
//                .map(college -> {
//                    // 强制类型转换
//                    org.example.postgraduaterecommendation.dox.College collegeObj =
//                            (org.example.postgraduaterecommendation.dox.College) college;
//                    List<Major> majors = majorRepository.findByCollegeId(collegeObj.getId());
//
//                    return CollegeDTO.builder()
//                            .college(collegeObj)
//                            .majors(majors)
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
public List<CollegeDTO> listCollegesAndMajors() {
    List<?> colleges = collegeRepository.findAll(); // 注意这里返回的是 raw type

    return ((List<?>) colleges).stream()
            .map(college -> {
                org.example.postgraduaterecommendation.dox.College collegeObj =
                        (org.example.postgraduaterecommendation.dox.College) college;
                List<Major> majors = majorRepository.findByCollegeId(collegeObj.getId());
                return CollegeDTO.builder()
                        .college(collegeObj)
                        .majors(majors)
                        .build();
            })
            .collect(Collectors.toList());
}


    @CacheEvict(value = {"majorCategory", "majors"}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void addMajor(Major major) {
        majorRepository.save(major);
    }

    @Cacheable(value = "majors", key = "#mcid")
    public List<Major> listMajors(long mcid) {
        return majorRepository.findByMajorCategoryId(mcid);
    }

    // 辅导员是否在类别下
//    public boolean checkInMajorCateory(long uid, long mcid) {
//        return userCategoryRepository.checkInMajorCategory(uid, mcid);
//    }
    public boolean checkInMajorCateory(long uid, long mcid) {
        Integer existsFlag = userCategoryRepository.checkInMajorCategory(uid, mcid);
        return existsFlag != null && existsFlag == 1;
    }

    public MajorCategory getMajorCategory(long mcid) {
        Optional<MajorCategory> majorCategoryOpt = majorCategoryRepository.findById(mcid);
        return majorCategoryOpt.orElse(null);
        // 或抛异常：return categoryOpt.orElseThrow(() -> new RuntimeException("类别不存在"));
    }
}
