package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.StudentItemLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StudentItemLogRepository extends CrudRepository<StudentItemLog, Long> {

    //根据学生ID和学生指标ID查询审核日志
    @Query(value = """
            select t1.* from student_item_log t1 
            join student_item t2 on t1.student_item_id = t2.id 
            where t2.user_id = :uid and t1.student_item_id = :stuitemid
            """)
    List<StudentItemLog> find(@Param("uid") long uid, @Param("stuitemid") long stuitemid);

}
