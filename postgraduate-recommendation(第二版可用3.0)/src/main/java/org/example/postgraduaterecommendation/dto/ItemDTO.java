package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private Long majorCategoryId;
    private Float maxPoints;
    private Integer maxItems;
    private Long parentId;
    private String comment;

    private List<ItemDTO> items;
}