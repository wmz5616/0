package com.zemcho.guzhe.config.execption;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Map;

/*
 * @author Ryan
 * @title: GlobalFilterExceptionHandler
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/7/2 0002 16:12
 */


@RestController
public class GlobalFilterExceptionHandler extends BasicErrorController {
    public GlobalFilterExceptionHandler() {
        super(new DefaultErrorAttributes(),new ErrorProperties());
    }

    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request){
        /*Map<String, Object> map = super.getErrorAttributes(request, true);
        Object trace = map.get("trace");*/
        throw new TokenExpiredException("token验证失败或无权限访问", Instant.now());
    }
}
