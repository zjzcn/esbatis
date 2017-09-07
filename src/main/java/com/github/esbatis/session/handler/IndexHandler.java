package com.github.esbatis.session.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.session.ResultHandler;

public class IndexHandler implements ResultHandler<Object> {

    private Class<?> returnClass;

    public IndexHandler(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    @Override
    public Object handleResult(String result) {
        JSONObject resultJO = JSON.parseObject(result);
        String id = resultJO.getString("_id");
        if (id == null) {
            return null;
        } else if (returnClass == String.class) {
            return id;
        } else if(returnClass == Short.class) {
            return Short.valueOf(id);
        } else if(returnClass == Integer.class) {
            return Integer.valueOf(id);
        } else if(returnClass == Long.class) {
            return Long.valueOf(id);
        } else {
            return id;
        }
    }
}
