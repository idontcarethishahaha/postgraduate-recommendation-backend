package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.WeightedScore;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface WeightedScoreRepository extends CrudRepository<WeightedScore, Long> {

    //更新加权得分、排名、审核状态
    @Modifying // 清空一级缓存，避免脏数据
    @Transactional
    @Query(value = """
            update weighted_score t1
            set t1.score=:score, t1.ranking=:ranking, t1.verified=:verified
            where t1.id=:uid
            """)
    int updateWeightedScore(
            @Param("uid") long uid,
            @Param("score") float score,
            @Param("ranking") int ranking,
            @Param("verified") int verified
    );

}