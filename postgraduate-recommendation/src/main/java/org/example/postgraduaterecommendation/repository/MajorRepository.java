package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.Major;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wuwenjin
 */
@Repository
public interface MajorRepository extends CrudRepository<Major, Long> {
    //同一类别下是否存在同名专业
    boolean existsByMajorCategoryIdAndName(Long majorCategoryId, String name);
    //根据类别ID查询所有专业
    List<Major> findByMajorCategoryId(Long majorCategoryId);
    //根据ID和学院ID查询（用于权限验证）
    Optional<Major> findByIdAndCollegeId(Long id, Long collegeId);
    //类别下是否有专业
    boolean existsByMajorCategoryId(Long majorCategoryId);
    //按学院和类别查询专业
    List<Major> findByCollegeIdAndMajorCategoryId(Long collegeId, Long majorCategoryId);
    //根据学院ID查询所有专业
    List<Major> findByCollegeId(Long collegeId);
}

