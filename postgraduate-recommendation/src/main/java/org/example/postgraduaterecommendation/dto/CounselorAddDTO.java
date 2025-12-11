package org.example.postgraduaterecommendation.dto;

/*
 * @author wuwenjin
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorAddDTO {
    //private Long collegeId;
    // private Long majorCategoryId;
    private String majorCategoryId;
    private String name;
    private String account;
    private String password;
    private String tel;
}

