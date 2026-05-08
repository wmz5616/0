package com.zemcho.guzhe.config.execption;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zemcho.guzhe.common.Result;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * @author Ryan
 * @title: GlobalExceptionHandler
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/7/2 0002 16:27
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = TokenExpiredException.class)
    public Result tokenErrorHandler(TokenExpiredException e) {
        logger.error(ExceptionUtils.getFullStackTrace(e));
        return new Result(10005, e.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)
    public Result sqlErrorHandler(SQLException e) {
        logger.error(ExceptionUtils.getFullStackTrace(e));
        return new Result(10006, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception e) {
        logger.error(ExceptionUtils.getFullStackTrace(e));
        return new Result(10004, "服务器异常，请联系管理员！");
    }

    /**
     * 拦截valid参数校验返回的异常，并转化成基本的返回样式
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result dealMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error(ExceptionUtils.getFullStackTrace(e));
        String message =
                e.getBindingResult().getAllErrors().stream().map(s -> s.getDefaultMessage()).collect(Collectors.joining("、"));
        return new Result(10002, message);
    }
}
