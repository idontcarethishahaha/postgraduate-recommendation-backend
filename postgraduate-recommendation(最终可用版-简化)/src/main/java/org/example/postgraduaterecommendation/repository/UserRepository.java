package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.User;
import org.example.postgraduaterecommendation.dto.UserInfoDTO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    // 根据账号查询用户
    Optional<User> findByAccount(@Param("account") String account);

    // 学院管理员用户信息（关联user_category，只查绑定的类别）
    @Query("""
       select t1.id, 
              t1.name, 
              ifnull(t2.name, '') as college_name, 
              ifnull(
                  JSON_ARRAY(GROUP_CONCAT(distinct t3.name)), 
                  JSON_ARRAY()
              ) as categories
       from user t1
       left join college t2 on t1.college_id = t2.id
       left join user_category t4 on t4.user_id = t1.id
       left join major_category t3 on t3.id = t4.major_category_id
       where t1.id=:id
       group by t1.id, t1.name, t2.name;
       """)
    Optional<UserInfoDTO> findCollegeAdminUserInfo(@Param("id") long id);

    // 辅导员用户信息（distinct去重，空值返回空数组）
    @Query("""
        select t1.id, 
               t1.name, 
               ifnull(t2.name, '') as college_name, 
               ifnull(
                   JSON_ARRAY(GROUP_CONCAT(distinct t3.name)), 
                   JSON_ARRAY()
               ) as categories
        from user t1
        left join college t2 on t1.college_id = t2.id
        left join user_category t4 on t4.user_id = t1.id
        left join major_category t3 on t3.id = t4.major_category_id and t3.college_id = t2.id
        where t1.id=:id
        group by t1.id, t1.name, t2.name;
        """)
    Optional<UserInfoDTO> findCounselorUserInfo(@Param("id") long id);

    // 学生用户信息
    @Query("""
        select t1.id, 
               t1.name, 
               ifnull(t2.name, '') as college_name, 
               -- 用GROUP_CONCAT转JSON
               ifnull(
                   JSON_ARRAY(GROUP_CONCAT(distinct t3.name)), 
                   JSON_ARRAY()
               ) as categories, 
               ifnull(t4.name, '') as major_name
        from user t1
        left join college t2 on t1.college_id = t2.id
        left join major_category t3 on t3.id = t1.major_category_id
        left join major t4 on t4.id = t1.major_id
        where t1.id=:id
        group by t1.id, t1.name, t2.name, t4.name;
        """)
    Optional<UserInfoDTO> findStudentUserInfo(@Param("id") long id);

    // 修改密码（按用户ID）
    @Modifying
    @Query("""
            update user u set u.password=:password where u.id=:uid;
            """)
    int updatePassword(@Param("uid") long uid, @Param("password") String password);

    // 修改密码（按学院ID+账号）
    @Modifying
    @Query("""
            update user u set u.password=:password where u.college_id=:cid and u.account=:account;
            """)
    int updatePassword(@Param("cid") long cid,
                       @Param("account") String account,
                       @Param("password") String password);

    // 查询文件目录名（空值处理，避免返回null）
    @Query("""
            select concat_ws('-', ifnull(u.name, ''), ifnull(u.account, '')) 
            from user u where u.id=:uid;
            """)
    Optional<String> findFileDirectoryName(@Param("uid") long uid);

    // 查找学院管理员
    List<User> findByCollegeIdAndRole(Long collegeId, String role);
}