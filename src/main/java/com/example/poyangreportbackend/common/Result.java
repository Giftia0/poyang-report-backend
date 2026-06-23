package com.example.poyangreportbackend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static Result success(String msg){
        Result result = new Result();
        result.code = Code.OPERATION_SUCCEEDED;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> success(String msg, T data){
        Result<T> result = new Result<T>();
        result.code = Code.OPERATION_SUCCEEDED;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<T>();
        result.code = Code.OPERATION_FAILED;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(String msg,int code){
        Result<T> result = new Result<T>();
        result.code = code;
        result.msg = msg;
        return result;
    }
}
