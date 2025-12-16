package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.dox.Major;
import org.example.postgraduaterecommendation.dox.MajorCategory;

import java.util.List;

/**
 * @author wuwenjin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MajorCategoryDTO {
    private MajorCategory majorCategory;
    private List<Major> majors;
}
