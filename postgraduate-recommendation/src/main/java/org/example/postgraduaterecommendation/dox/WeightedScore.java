package org.example.postgraduaterecommendation.dox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weighted_score")
public class WeightedScore implements Persistable<Long> {

    // 审核认定学生加权成绩
    public static final int VERIFIED = 1;
    public static final int UNVERIFIED = 0;

    @Id
    private Long id;
    private Float score;
    private Integer ranking;
    private Integer verified;

    private String logs;

    @Transient
    @JsonIgnore
    private boolean isNew;

    public void setNew() {
        isNew = true;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}