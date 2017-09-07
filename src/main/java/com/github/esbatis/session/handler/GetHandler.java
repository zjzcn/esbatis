package com.github.esbatis.session.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.session.ResultHandler;

public class GetHandler implements ResultHandler<Object> {

    private Class<?> returnClass;

    public GetHandler(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    @Override
    public Object handleResult(String result) {
        JSONObject resultJO = JSON.parseObject(result);
        Object bean = resultJO.getObject("_source", returnClass);

        return bean;
    }
}
