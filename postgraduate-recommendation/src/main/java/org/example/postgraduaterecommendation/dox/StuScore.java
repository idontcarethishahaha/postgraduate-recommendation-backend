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

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stu_score")
public class StuScore {
    @Id
    @CreatedBy
    private Long id;

    private Long userId;
    private Integer majorRank;
    private BigDecimal weightedScore;
    private Integer status;
    @ReadOnlyProperty
    private LocalDateTime createTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;

    public static final Integer STATUS_PENDING = 0; //待审核
    public static final Integer STATUS_APPROVED = 1;//已认定
}

