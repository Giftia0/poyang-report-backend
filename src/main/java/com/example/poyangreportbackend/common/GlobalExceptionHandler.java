package com.example.poyangreportbackend.common;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 定义要捕获的异常 可以多个 @ExceptionHandler({})     *
     *
     * @param request  request
     * @param e        exception
     * @param response response
     * @return 响应结果
     */

    @ExceptionHandler(ConstraintViolationException.class)
    public Result verifyExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        String msg = e.getMessage();
        msg = msg.substring(msg.indexOf(' ') + 1);
        System.out.println();
        return Result.error(msg);
    }

}
