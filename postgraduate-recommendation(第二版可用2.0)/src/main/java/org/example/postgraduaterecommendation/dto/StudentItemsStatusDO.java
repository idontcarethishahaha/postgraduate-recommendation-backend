package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentItemsStatusDO {
    private Long userId;
    private String userName;
    private String tel;
    private Float score;
    private Integer ranking;
    private Integer verified;
    private Float totalPoint;
    private Integer totalCount;
    private Integer pendingReviewCount;
    private Integer rejectedCount;
    private Integer pendingModificationCount;
    private Integer confirmedCount;
}
