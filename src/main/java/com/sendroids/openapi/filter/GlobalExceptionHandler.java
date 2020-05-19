package com.sendroids.openapi.filter;

import com.sendroids.openapi.domain.ResponseApi;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseApi BindExceptionHandler(BindException e, HttpServletResponse response) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return dealResponse(message, response);
    }

    //处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseApi ConstraintViolationExceptionHandler(ConstraintViolationException e, HttpServletResponse response) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        return dealResponse(message, response);
    }

    //处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseApi MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletResponse response) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return dealResponse(message, response);
    }

    private ResponseApi dealResponse(
            final String message,
            final HttpServletResponse response
    ) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        ResponseApi responseApi = new ResponseApi();
        responseApi.setCode(400L);
        responseApi.setMessage(message);
        responseApi.setContent(new ArrayList<>());
        return responseApi;
    }
}

