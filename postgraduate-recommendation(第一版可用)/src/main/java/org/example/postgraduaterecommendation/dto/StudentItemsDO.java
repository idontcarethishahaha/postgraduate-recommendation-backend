package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentItemsDO {
    private Long id;
    private Long userId;
    private Long rootItemId;
    private Long studentItemFileId;
    private Long itemId;
    private long itemParentId;
    private String name;
    private String comment;
    private String status;
    private String itemName;
    private String itemComment;
    private Float point;
    private Float maxPoints;
    private Integer maxItems;
    private String path;
    private String filename;
}