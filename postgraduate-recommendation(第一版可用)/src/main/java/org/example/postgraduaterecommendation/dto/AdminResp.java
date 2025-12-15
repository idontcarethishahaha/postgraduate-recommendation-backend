package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.dox.MajorCategory;
import org.example.postgraduaterecommendation.dox.User;
import java.util.List;

/**
 * @author wuwenjin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResp {
    private MajorCategory majorCategory;
    private List<User> users;
}