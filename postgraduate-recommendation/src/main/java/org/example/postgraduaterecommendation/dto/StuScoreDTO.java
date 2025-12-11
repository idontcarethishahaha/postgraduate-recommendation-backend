package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wuwenjin
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StuScoreDTO {
    private BigDecimal weightedScore;
    private Integer majorRank;
}
