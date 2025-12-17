package org.example.postgraduaterecommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.postgraduaterecommendation.dox.WeightedScore;
import org.example.postgraduaterecommendation.dox.WeightedScoreLog;
import org.example.postgraduaterecommendation.repository.WeightedScoreLogRepository;
import org.example.postgraduaterecommendation.repository.WeightedScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author wuwenjin
 */
@Service
@RequiredArgsConstructor
public class WeightedScoreService {

    private final WeightedScoreRepository weightedScoreRepository;
    private final WeightedScoreLogRepository weightedScoreLogRepository;

    //查询加权得分
    public WeightedScore getWeightedScore(long uid) {
        Optional<WeightedScore> weightedScore = weightedScoreRepository.findById(uid);
        if (weightedScore.isEmpty()) {
            throw new RuntimeException("加权得分记录不存在");
        }
        return weightedScore.get();
    }

    //添加加权得分
    @Transactional(rollbackFor = Exception.class)
    public WeightedScore addWeightedScore(WeightedScore weightedScore) {
        weightedScore.setNew();
        return weightedScoreRepository.save(weightedScore);
    }

    //更新加权得分
    @Transactional(rollbackFor = Exception.class)
    public void updateWeightedScore(long uid, float score, int ranking, int verified) {
        weightedScoreRepository.updateWeightedScore(uid, score, ranking, verified);
    }

    //更新加权得分 + 保存评分日志
    @Transactional(rollbackFor = Exception.class)
    public void updateWeightedScore(long sid, float score, int ranking, int verified, WeightedScoreLog log) {
        // 先更新加权得分
        int affectedRows = weightedScoreRepository.updateWeightedScore(sid, score, ranking, verified);
        // 受影响行数>0 时保存日志
        if (affectedRows > 0) {
            weightedScoreLogRepository.save(log);
        }
    }

}
