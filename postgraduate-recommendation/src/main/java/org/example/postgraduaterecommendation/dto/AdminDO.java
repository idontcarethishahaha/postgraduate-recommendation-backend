package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuwenjin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDO {
    private String majorCategoryName;
    private String userName;
    private Long majorCategoryId;
    private Long userId;
    //private Long account;
}