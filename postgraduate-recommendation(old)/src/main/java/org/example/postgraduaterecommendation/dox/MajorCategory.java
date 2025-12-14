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

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "major_category")
public class MajorCategory {
    @Id
    @CreatedBy
    private Long id;

    private String name;

    private Long collegeId;

    private String calculationRule; //在数据库中是json

    @ReadOnlyProperty
    private LocalDateTime createTime;

    @ReadOnlyProperty
    private LocalDateTime updateTime;
}