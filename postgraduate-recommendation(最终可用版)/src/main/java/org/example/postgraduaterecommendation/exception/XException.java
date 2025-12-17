package org.example.postgraduaterecommendation.exception;

import lombok.*;

/**
 * @author wuwenjin
 */
// 自定义异常
// 继承自非受检异常 unchecked
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class XException extends RuntimeException {
    private Code code; // 通用
    private int codeNum;// 自定义补充
    private String message;

}