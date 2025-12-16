package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.Major;
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
public interface MajorRepository extends CrudRepository<Major, Long> {


    @Query("""
            select t1.* from major t1 where t1.major_category_id=:mcid;
            """)
    List<Major> findByMajorCategoryId(@Param("mcid") long mcid);

    @Query("""
            select t3.* from major_category t2, major t3
            where t2.id=t3.major_category_id and t2.college_id=:cid;
            """)
    List<Major> findByCollegeId(@Param("cid") long cid);


    @Query("""
            select concat_ws('/',t3.name, t2.name, t1.name) from major t1, major_category t2, college t3
            where t1.major_category_id=t2.id and t2.college_id=t3.id and t1.id=:mid;
            """)
    Optional<String> findFileDirectoryName(@Param("mid") long mid);
}


