package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.WeightedScoreLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightedScoreLogRepository extends CrudRepository<WeightedScoreLog, Long> {
}