package com.sjx.a_p.config;

import com.sjx.a_p.model.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 统一异常处理
@ControllerAdvice
@ResponseBody
public class ErrorHandler {

    @ExceptionHandler
    // 可以在注解中指定特定的异常，或者在 handler 方法参数中指定
    // @ExceptionHandler(NullPointerException.class)
    public Result handler(Exception e){
        return Result.fail(e.getMessage());
    }
}
