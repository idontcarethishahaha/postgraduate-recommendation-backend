package org.example.postgraduaterecommendation.dto;

/*
 * @author wuwenjin
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long indicatorId;
    private String itemName;
    private String description;
    //private List<FileEvidence> evidences;
}
