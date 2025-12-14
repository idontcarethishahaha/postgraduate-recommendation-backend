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
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegisterDTO {
    private Long collegeId;
    private Long majorId;
    private String name;
    private String tel;
    private String account;
    private String password;
}
