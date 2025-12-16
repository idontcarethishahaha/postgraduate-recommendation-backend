package org.example.postgraduaterecommendation.dox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
//@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_item")
public class StudentItem {

    public interface Status {
        String PENDING_REVIEW = "kf7u";//已提交
        String REJECTED = "op81";//已驳回
        String PENDING_MODIFICATION = "Tg9i";//待修改
        String CONFIRMED = "yF2m";//已认定
    }

    @Id
    @CreatedBy
    private Long id;
    private Long userId;
    //@Column(name = "root_item_id")
    private Long rootItemId;
    private Long itemId;
    private Float point;
    private String name;
    private String comment;
    private String status;

    @Transient
    private List<StudentItemFile> files;

    @ReadOnlyProperty
    private LocalDateTime createTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
