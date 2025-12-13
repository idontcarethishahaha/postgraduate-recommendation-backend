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

/**
 * @author wuwenjin
 */
//持久层接口
@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByAccount(String account);
    boolean existsByAccount(String account);
    List<User> findByCollegeIdAndRole(Long collegeId, String role);
    Optional<User> findById(Long id);

    // =============================================

    // 根据账号查询用户
    // User findByAccount(@Param("account") String account);

    // 通用用户信息查询
    @Query("""
            select t1.id, 
                   t1.name, 
                   t2.name as coll_name,
                   json_arrayagg(distinct t3.name) as categories,
                   t4.name as major_name
            from user t1
            left join college t2 on t1.college_id = t2.id
            -- 关联专业类别（major_category）：通过导员/学生/专业多层关联
            left join counselor_info t5 on t1.id = t5.user_id
            left join major_category t3 on t5.major_category_id = t3.id
            left join student_info t6 on t1.id = t6.user_id
            left join major t4 on t6.major_id = t4.id
            left join major_category t7 on t4.major_category_id = t7.id
            where t1.id=:id 
            group by t1.id, t2.name, t4.name;
            """)
    UserInfoDTO find(@Param("id") long id);

    // 学院管理员用户信息查询（关联学院下所有专业类别）
    @Query("""
           select t1.id,
                  t1.name, 
                  t2.name as coll_name, 
                  JSON_ARRAYAGG(distinct t3.name) as categories
           from user t1
           left join college t2 on t1.college_id = t2.id
           left join major_category t3 on t2.id = t3.college_id
           where t1.id=:id
           group by t1.id, t2.name;
           """)
    UserInfoDTO findCollegeAdminUserInfo(@Param("id") long id);

    // 导员信息查询（关联负责的专业类别）
    @Query("""
            select t1.id, 
                   t1.name, 
                   t2.name as coll_name, 
                   JSON_ARRAYAGG(distinct t3.name) as categories
            from user t1
            left join college t2 on t1.college_id = t2.id
            left join counselor_info t4 on t1.id = t4.user_id
            left join major_category t3 on t4.major_category_id = t3.id
            where t1.id=:id
            group by t1.id, t2.name;
            """)
    UserInfoDTO findConselorUserInfo(@Param("id") long id);

    // 学生用户信息查询（关联所属专业+专业类别）
    @Query("""
            select t1.id, 
                   t1.name, 
                   t2.name as coll_name, 
                   JSON_ARRAYAGG(distinct t3.name) as categories, 
                   t4.name as major_name
            from user t1
            left join college t2 on t1.college_id = t2.id
            left join student_info t5 on t1.id = t5.user_id
            left join major t4 on t5.major_id = t4.id
            left join major_category t3 on t4.major_category_id = t3.id
            where t1.id=:id
            group by t1.id, t2.name, t4.name;
            """)
    UserInfoDTO findStudentUserInfo(@Param("id") long id);
//===================================================================
    // 更新用户密码（按用户ID）
    @Modifying
    @Query("""
            update user u set u.password=:password where u.id=:uid;
            """)
    void updatePassword(@Param("uid") long uid, @Param("password") String password);

    // 更新用户密码（按学院ID+账号）
    @Modifying
    @Query("""
            update user u set u.password=:password where u.college_id=:collid and u.account=:account;
            """)
    Integer updatePassword(@Param("collid") long collid,
                           @Param("account") String account,
                           @Param("password") String password);

    // 查询用户文件目录名（姓名-账号）
    @Query("""
            select concat_ws('-', u.name, u.account) from user u where u.id=:uid;
            """)
    String findFileDirectoryName(@Param("uid") long uid);

    // 按专业ID查询用户（学生）
    @Query("""
            select u.* from user u
            left join student_info si on u.id = si.user_id
            where si.major_id=:majorid;
            """)
    List<User> findByMajorId(@Param("majorid") long majorid);
}