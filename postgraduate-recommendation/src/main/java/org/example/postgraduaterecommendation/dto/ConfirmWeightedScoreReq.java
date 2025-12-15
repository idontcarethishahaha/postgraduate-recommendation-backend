package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.dox.WeightedScore;
import org.example.postgraduaterecommendation.dox.WeightedScoreLog;

/**
 * @author wuwenjin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmWeightedScoreReq {
    private WeightedScore weightedScore;
    private WeightedScoreLog log;
}
