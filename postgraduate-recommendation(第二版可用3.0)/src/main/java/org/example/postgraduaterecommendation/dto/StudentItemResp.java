package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.dox.Item;
import org.example.postgraduaterecommendation.dox.StudentItem;
import org.example.postgraduaterecommendation.dox.StudentItemFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentItemResp {
    private Long id;
    private Long userId;
    private Long rootItemId;
    private Long itemId;
    private String name;
    private Float point;
    private String comment;
    private String status;

    private String itemName;
    private Float maxPoints;
    private Integer maxItems;
    private Long itemParentId;
    private String itemComment;

    private List<StudentItemFile> files;
    private Item item;
    private List<StudentItem>  studentItems;
}
