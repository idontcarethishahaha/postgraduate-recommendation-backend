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
@Table(name = "student_item_log")
public class StudentItemLog {
    @Id
    @CreatedBy
    private Long id;
    private Long studentItemId;
    private Long userId;
    private String comment;

    @ReadOnlyProperty
    private LocalDateTime createTime;
}
