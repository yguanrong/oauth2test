package com.example.exception;

import com.example.dto.ServerResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@ControllerAdvice
@Slf4j
public class IntellifExceptionHandler {

    /**
     * 全局异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ServerResp handleException(Exception ex) {

        //断言
        if (ex instanceof RuntimeException) {
            log.info("RuntimeException异常:{}", ex);
            RuntimeException exception = (RuntimeException) ex;
            return ServerResp.error(exception.getMessage());
        }

        //JSR校验异常
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
            List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
            FieldError error = fieldErrors.get(0);
            return ServerResp.error(error.getDefaultMessage());
        }

        //其他异常
        ex.printStackTrace();
        log.info("Exception未知异常:{}", ex.getMessage(),ex);
        return ServerResp.error(ex.getMessage());
    }

}