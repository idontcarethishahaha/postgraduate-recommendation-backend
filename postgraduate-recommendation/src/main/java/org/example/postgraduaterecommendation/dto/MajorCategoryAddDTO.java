package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuwenjin
 */
/*
为学院添加类别，需要学院的id,加上要添加的类别名
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MajorCategoryAddDTO {
    //移除前端传递的学院id，完全由后端从token中获取
    //private Long collegeId;
    private String name;
    private String calculationRule;
}
