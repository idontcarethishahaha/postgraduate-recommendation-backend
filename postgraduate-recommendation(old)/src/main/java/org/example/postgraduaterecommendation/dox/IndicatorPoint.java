package org.example.postgraduaterecommendation.dox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="indicator_points")
public class IndicatorPoint {
    @Id
    @CreatedBy
    private Long id;

    private Long majorCategoryId;
    private String name;
    private Integer level;
    private String description;
    private BigDecimal maxScore;
    private Integer itemUpperLimit;
    private Long parentId;
    private Boolean isLeaf;

    @ReadOnlyProperty
    private LocalDateTime createTime;

    @ReadOnlyProperty
    private LocalDateTime updateTime;
}