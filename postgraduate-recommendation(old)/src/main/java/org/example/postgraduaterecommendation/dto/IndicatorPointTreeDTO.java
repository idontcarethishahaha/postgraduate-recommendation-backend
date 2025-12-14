package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorPointTreeDTO {
    private Long id;
    private Long majorCategoryId;
    private String name;
    private Integer level;
    private String description;
    private BigDecimal maxScore;
    private Integer itemUpperLimit;
    private Boolean isLeaf;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<IndicatorPointTreeDTO> children;//子节点
}