package org.example.postgraduaterecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.dox.StudentItem;
import org.example.postgraduaterecommendation.dox.StudentItemLog;

/**
 * @author wuwenjin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentItemReq {
    private StudentItem studentItem;
    private StudentItemLog log;
}