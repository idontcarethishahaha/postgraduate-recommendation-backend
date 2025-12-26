package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.StudentItem;
import org.example.postgraduaterecommendation.dto.StudentItemsDO;
import org.example.postgraduaterecommendation.dto.StudentItemsStatusDO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author wuwenjin
 */
@Repository
public interface StudentItemRepository extends CrudRepository<StudentItem, Long> {

    //根据用户ID和学生指标ID查询
    @Query(value = """
            select * from student_item t1 where t1.id=:id and t1.user_id=:uid;
            """)
    Optional<StudentItem> findByUserId(@Param("uid") long uid, @Param("id") long id);

    //根据用户ID更新状态
    @Modifying
    @Transactional
    @Query(value = """
            update student_item t1 set t1.status=:status where t1.user_id=:uid
            """)
    int updateStatus(@Param("uid") long uid, @Param("status") String status);

    //根据根指标ID+用户ID查询学生指标（含附件/指标模板信息）
    @Query(value = """
            select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment,
                   t1.status,
                   ifnull(t2.filename, '') as filename, ifnull(t2.id, 0) as student_item_file_id,
                   t3.name as item_name, ifnull(t3.comment, '') as item_comment, t3.max_points, ifnull(t3.max_items, 0) as max_items
            from student_item t1 
            left join student_item_file t2 on t1.id = t2.student_item_id
            join item t3 on t3.id=t1.item_id
            where t1.root_item_id=:rootitemid and t1.user_id=:uid 
            order by t1.item_id;
            """)
    List<StudentItemsDO> findByRootItemId(@Param("uid") long uid, @Param("rootitemid") long rootitemid);

    //根据用户ID查询所有学生指标（含附件/指标模板信息）
    @Query(value = """
            select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment, t1.status,
            ifnull(t2.filename, '') as filename, ifnull(t2.id, 0) as student_item_file_id,
            t3.id as item_id, t3.name as item_name, ifnull(t3.comment, '') as item_comment, t3.max_points, ifnull(t3.max_items, 0) as max_items, t3.parent_id as item_parent_id
            from student_item t1 
            left join student_item_file t2 on t1.id = t2.student_item_id
            join item t3 on t3.id=t1.item_id
            where t1.user_id=:uid 
            order by t3.id;
            """)
    List<StudentItemsDO> findByUserId(@Param("uid") long uid);

    //根据用户ID+指标ID更新信息
    @Modifying
    @Transactional
    @Query(value = """
            update student_item t1 set t1.name=:name, t1.comment=:comment, t1.status=:status
            where t1.user_id=:uid and t1.id=:id
            """)
    int updateByUserId(@Param("uid") long uid,
                       @Param("id") long id,
                       @Param("name") String name,
                       @Param("comment") String comment,
                       @Param("status") String status);

    //根据专业ID查询学生指标统计信息
    @Query(value = """
            select
                u.id as user_id,
                u.name as user_name,
                ifnull(u.tel, '') as mobile,
                ifnull(ws.score, 0) as score,
                ifnull(ws.ranking, 0) as ranking,
                ifnull(ws.verified, 0) as verified,
                coalesce(sum(si.point), 0) as total_point,
                count(distinct u.id) as total_count,
                coalesce(sum(case when si.status = :sub then 1 else 0 end), 0) as pending_review_count,
                coalesce(sum(case when si.status = :rej then 1 else 0 end), 0) as rejected_count,
                coalesce(sum(case when si.status = :pend then 1 else 0 end), 0) as pending_modification_count,
                coalesce(sum(case when si.status = :conf then 1 else 0 end), 0) as confirmed_count
            from
                `user` u 
                left join weighted_score ws on ws.id=u.id
                left join student_item si on u.id = si.user_id
            where
                u.major_id =:majorid
            group by
                u.id, u.name, u.tel, ws.score, ws.ranking, ws.verified
            order by
                u.id;
            """)
    List<StudentItemsStatusDO> findStudentItemsInfos(@Param("majorid") long majorid,
                                                     @Param("sub") String sub,
                                                     @Param("rej") String rej,
                                                     @Param("pend") String pend,
                                                     @Param("conf") String conf);

    //根据用户ID查询学生指标统计信息
    @Query(value = """
            select
                u.id as user_id,
                u.name as user_name,
                ifnull(ws.score, 0) as score,
                ifnull(ws.ranking, 0) as ranking,
                ifnull(ws.verified, 0) as verified,
                coalesce(sum(si.point), 0) as total_point,
                count(distinct u.id) as total_count,
                coalesce(sum(case when si.status = :sub then 1 else 0 end), 0) as pending_review_count,
                coalesce(sum(case when si.status = :rej then 1 else 0 end), 0) as rejected_count,
                coalesce(sum(case when si.status = :pend then 1 else 0 end), 0) as pending_modification_count,
                coalesce(sum(case when si.status = :conf then 1 else 0 end), 0) as confirmed_count
            from
                `user` u 
                left join weighted_score ws on ws.id=u.id  -- 改为left join，避免用户无评分时查不到
                left join student_item si on u.id = si.user_id
            where
                u.id =:uid
            group by
                u.id, u.name, ws.score, ws.ranking, ws.verified
            order by
                u.id;
            """)
    Optional<StudentItemsStatusDO> findStudentItemsInfo(@Param("uid") long uid,
                                                        @Param("sub") String sub,
                                                        @Param("rej") String rej,
                                                        @Param("pend") String pend,
                                                        @Param("conf") String conf);

    //更新指标分数和状态
    @Modifying
    @Transactional
    @Query(value = """
            update student_item t1 set t1.point=:point, t1.status=:status where t1.id=:id
            """)
    int update(@Param("id") long id,
               @Param("point") Float point,
               @Param("status") String status);
}
