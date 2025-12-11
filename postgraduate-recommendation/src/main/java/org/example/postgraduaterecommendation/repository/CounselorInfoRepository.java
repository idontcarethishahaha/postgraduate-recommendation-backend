package org.example.postgraduaterecommendation.repository;

/*
 * @author wuwenjin
 */


import org.example.postgraduaterecommendation.dox.CounselorInfo;
import org.springframework.data.jdbc.repository.query.Query;  // 保持 JDBC
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounselorInfoRepository extends CrudRepository<CounselorInfo, Long> {
    Optional<CounselorInfo> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
