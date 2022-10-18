package com.ai.aiface.util;

import lombok.Data;

/**
 * api通用返回类，返回格式 {"code": xxx, "message":xxx "data": xxx }
 */
@Data
public class Result {

    // 状态码
    private int code;

    //提示信息
    private String message;

    // 数据内容
    private Object data;

    // 通用静态工厂方法
    //自己用200正常 404 500
    public static Result success(Object data){
        // 设置默认code为0，表示访问正常
        return Result.add(200,"成功", data);
    }
    public static Result fail4(Object data){
        // 设置默认code为0，表示访问正常
        return Result.add(401,"无数据", data);
    }
    public static Result fail5(Object data){
        // 设置默认code为0，表示访问正常
        return Result.add(500,"数据异常", data);
    }


    // 静态工厂方法，重写Code和Data
    public static Result add(int code,String message,Object data){
        Result returnType = new Result();
        returnType.setCode(code);
        returnType.setMessage(message);
        returnType.setData(data);
        return returnType;
    }
}
