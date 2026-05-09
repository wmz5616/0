package com.zemcho.guzhe.common;

import lombok.Data;

import java.util.HashMap;

/**
 * @author Ryan
 * @title: Result
 * @projectName master
 * @description: 请求返回对象
 * @date 2020/7/22 0022 10:47
 */
@Data
public class Result {

    //错误码
    private int code;

    //提示信息
    private String msg;

    //具体的内容, total,pageNum,list
    private Object data;
    
    private Object reserveData;

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.reserveData = new HashMap<>();
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = new HashMap<>();
        this.reserveData = new HashMap<>();
    }
    
    public Result(int code, String msg, Object data,Object reserveData){
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.reserveData = reserveData;
    }

    public Result() {

    }

    public static Result success(String msg,Object data){
        Result result = new Result(10000,msg,data);
        return result;
    }

    public static Result success(String msg,Object data,Object reserveData){
        Result result = new Result(10000,msg,data,reserveData);
        return result;
    }

    public static Result error(String msg,Object data){
        Result result = new Result(10003,msg,data);
        return result;
    }

    public static Result success(String msg){
        Result result = new Result(10000,msg);
        return result;
    }

    public static Result error(String msg){
        Result result = new Result(10003,msg);
        return result;
    }
    
    public Boolean success(){
        if (this.code != 10000){
            return false;
        }
        return true;
    }
}
