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
@Table(name = "student_info")
public class StudentInfo {
    @Id
    @CreatedBy
    private Long id;

    private Long userId;

    private Long majorId;

    @ReadOnlyProperty
    private LocalDateTime createTime;

    @ReadOnlyProperty
    private LocalDateTime updateTime;
}