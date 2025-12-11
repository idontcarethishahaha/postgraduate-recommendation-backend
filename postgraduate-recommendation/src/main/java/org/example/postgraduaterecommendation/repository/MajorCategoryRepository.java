package org.example.postgraduaterecommendation.repository;


import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wuwenjin
 */
@Repository
public interface MajorCategoryRepository extends CrudRepository<MajorCategory, Long> {
    //boolean existsByName(String name);
    boolean existsByCollegeIdAndName(Long collegeId, String name);
    Optional<MajorCategory> findById(Long id);
    //按学院ID查询所有类别
    List<MajorCategory> findByCollegeId(Long collegeId);

}
