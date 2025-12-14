package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.StuScore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author wuwenjin
 */
@Repository
public interface StuScoreRepository extends CrudRepository<StuScore, Long> {
    Optional<StuScore> findByUserId(@Param("userId") Long userId);
    boolean existsByUserId(Long userId);
}
