package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.UserCategory;
import org.example.postgraduaterecommendation.dto.AdminDO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCategoryRepository extends CrudRepository<UserCategory, Long> {

    @Query("""
            select exists(
            select 1 from user_category t1 join user_category t2
            on t1.major_category_id=t2.major_category_id
            where t1.user_id=:sid and t2.user_id=:adminid);
            """)
    Integer checkUsersInSameMajorCategory(@Param("sid") long sid, @Param("adminid") long adminid);

//    @Query("""
//            select t2.name as category_name, t2.id as major_category_id,
//                   t3.name as user_name, t3.id as user_id
//            from user_category t1, major_category t2, user t3
//            where t1.major_category_id=t2.id
//              and t1.user_id=t3.id
//              and t2.college_id=:cid
//              and t3.role=:role
//            """)
//    List<AdminDO> findByCollegeId(@Param("cid") long cid, @Param("role") String role);
List<UserCategory> findByCollegeId(Long collegeId);

    //检查用户是否关联指定专业分类
    @Query("""
            select exists(select 1 from user_category t1 
                          where t1.major_category_id=:mcid and t1.user_id=:uid);
            """)
    Integer checkInMajorCategory(@Param("uid") long uid, @Param("mcid") long mcid);
}