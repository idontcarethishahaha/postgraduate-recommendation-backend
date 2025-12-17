package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.StudentItemFile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentItemFileRepository extends CrudRepository<StudentItemFile, Long> {

    void deleteByStudentItemId(long studentItemId);

    @Query(value = "select * from student_item_file t1 where t1.student_item_id=:stuitemid")
    List<StudentItemFile> findByStudentItemIds(@Param("stuitemid") long stuitemid);

    //根据用户ID和文件ID查询文件完整路径
    @Query("""
            SELECT (
                select concat_ws('/', t1.path, t1.filename)
                from student_item_file t1, student_item t2
                where t1.student_item_id = t2.id 
                and t2.user_id = :uid 
                and t1.id = :fileid
            )
            """)
    Optional<String> findPath(@Param("uid") long uid, @Param("fileid") long fileid);

//    // new
//    @Query("""
//        SELECT CONCAT(:rootDirectory, '/', t1.path, '/', t1.filename)
//        FROM student_item_file t1
//        JOIN student_item t2 ON t1.student_item_id = t2.id
//        WHERE t2.user_id = :uid AND t1.id = :fileid
//        """)
//    Optional<String> findFullPath(
//            @Param("rootDirectory") String rootDirectory,
//            @Param("uid") long uid,
//            @Param("fileid") long fileid
//    );

}
