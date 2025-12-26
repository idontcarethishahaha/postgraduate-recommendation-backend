package org.example.postgraduaterecommendation.repository;


import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wuwenjin
 */
@Repository
public interface MajorCategoryRepository extends CrudRepository<MajorCategory, Long> {

    //根据学院ID查询类别列表
    List<MajorCategory> findByCollegeId(long cid);


    // 查询学院下的专业类别ID
    @Query("""
            SELECT t1.id 
            FROM major_category t1 
            WHERE t1.college_id = :cid;
            """)
    List<Long> findMajorCategoryIdsByCollegeId(@Param("cid") long cid);

    // 根据用户ID查询绑定的专业类别
    @Query("""
            SELECT t1.* 
            FROM major_category t1 
            JOIN user_category t2 ON t1.id = t2.major_category_id 
            WHERE t2.user_id = :uid;
            """)
    List<MajorCategory> findByUserId(@Param("uid") long uid);
}
