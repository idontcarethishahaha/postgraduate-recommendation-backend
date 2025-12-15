package org.example.postgraduaterecommendation.controller;

/*
 * @author wuwenjin
 */
import lombok.extern.slf4j.Slf4j;
import org.example.postgraduaterecommendation.exception.Code;
import org.example.postgraduaterecommendation.exception.XException;
import org.example.postgraduaterecommendation.vo.ResultVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    // 一旦spring容器捕获该类型的异常，就交给这个方法来处理，并将其作为方法的参数
    @ExceptionHandler(XException.class)
    public ResultVO handleXException(XException e) {
        if (e.getCode() != null){
            return ResultVO.error(e.getCode());// 通用的
        }
        // 自定义的
        return ResultVO.error(e.getCodeNum(), e.getMessage()); // 方法重载
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResultVO handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return ResultVO.error(Code.BAD_REQUEST.getCode(), "唯一约束冲突" + exception.getMessage());
    }
    // 兜底处理
    @ExceptionHandler(Exception.class)
    public ResultVO handleException(Exception e) {
        return ResultVO.error(Code.ERROR, e.getMessage());
    }
}
// 封装，序列化，返给前端