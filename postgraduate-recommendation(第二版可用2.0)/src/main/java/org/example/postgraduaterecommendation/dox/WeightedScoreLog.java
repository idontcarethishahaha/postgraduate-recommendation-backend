package org.example.postgraduaterecommendation.dox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weighted_score_log")
public class WeightedScoreLog {
    @Id
    @CreatedBy
    private Long id;
    private Long studentId;
    private Long userId;

    @ReadOnlyProperty
    private LocalDateTime create_time;
}