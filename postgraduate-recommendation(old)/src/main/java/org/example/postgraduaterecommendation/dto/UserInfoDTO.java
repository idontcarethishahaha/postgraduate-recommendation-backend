package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuwenjin
 */
// 有的字段某些角色是没有的
// 前端要用来后端拿
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String name;//用户名
    private String collegeName;//学院名
    private String MajorCategories;//返给前端一个类别数组
    private String majorName;//专业名
}
