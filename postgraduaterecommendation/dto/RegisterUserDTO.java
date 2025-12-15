package org.example.postgraduaterecommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String account;
    private String name;
    private String tel;
    private Long collegeId;
    private Long majorId;

    private List<Long> majorCategoryIds;
    //@JsonProperty("catId")
    private Long majorCategoryId;
}
