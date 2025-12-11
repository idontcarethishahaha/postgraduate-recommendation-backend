package org.example.postgraduaterecommendation.dto;

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
public class IndicatorPointDTO {
    private Long majorCategoryId;
    private String name;
    private String description;
    private BigDecimal maxScore;
    private Integer itemUpperLimit;//限项
    private Long parentId;
    private Boolean isLeaf;
}