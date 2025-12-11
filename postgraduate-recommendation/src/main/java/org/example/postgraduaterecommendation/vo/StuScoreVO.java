package org.example.postgraduaterecommendation.vo;

/*
 * @author wuwenjin
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StuScoreVO {
    private Long id;
    private BigDecimal weightedScore;
    private Integer majorRank;
    private Integer status;
    private String statusDesc;
}
