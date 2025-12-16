package org.example.postgraduaterecommendation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postgraduaterecommendation.exception.Code;

import java.util.Map;

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

    //EMPTY 的 data 设为空 Map
    public static final ResultVO EMPTY = ResultVO.builder()
            .code(200)
            .data(Map.of())
            .build();

    public static ResultVO success() {
        return EMPTY;
    }

    // 处理 null 数据
    public static ResultVO success(Object data) {
        if (data == null) {
            data = Map.of();
        }
        return ResultVO.builder()
                .code(200)
                .data(data)
                .build();
    }

    // 支持自定义成功码
    public static ResultVO success(Code code, Map<String, Object> data) {
        if (data == null) {
            data = Map.of();
        }
        return ResultVO.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .data(data)
                .build();
    }

    // 通用错误码
    public static ResultVO error(Code code) {
        return ResultVO.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .build();
    }

    // 自定义错误码
    public static ResultVO error(int code, String message) {
        return ResultVO.builder()
                .code(code)
                .message(message)
                .build();
    }
}