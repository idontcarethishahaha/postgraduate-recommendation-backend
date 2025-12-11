package org.example.postgraduaterecommendation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.exception.Code;

/**
 * @author wuwenjin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO {
    private int code;
    private String message;//异常信息
    private Object data;//成功则返回200的业务码及数据

    public static final ResultVO EMPTY = ResultVO.builder()
            .code(200)
            .build();

    public static ResultVO ok() {
        return EMPTY;
    }

    public static ResultVO success(Object data) {
        return ResultVO.builder()
                .code(200)
                .data(data)
                .build();
    }

    public static ResultVO error(Code code) {
        return ResultVO.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .build();
    }

    //通用的不够了就显式声明
    public static ResultVO error(int code, String message) {
        return ResultVO.builder()
                .code(code)
                .message(message)
                .build();
    }
}