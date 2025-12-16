package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuwenjin
 */
// 添加辅导员用
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorDTO {
    private String account;
    private String name;
    private String tel;
    private Long collegeId;
    private Long majorCategoryId;
}
