package org.example.postgraduaterecommendation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorVO {
    //private Long id;
    private String id;
    private String name;
    private String account;
    private String tel;
    private String majorCategoryName;
    //private Long majorCategoryId;
    private String majorCategoryId;
    private LocalDateTime createTime;
}
