package com.github.esbatis.session.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.session.ResultHandler;

public class UpdateHandler implements ResultHandler<Object> {

    private Class<?> returnClass;

    public UpdateHandler(Class<?> returnClass) {
        this.returnClass = returnClass;
    }


    @Override
    public Object handleResult(String result) {
        JSONObject resultJO = JSON.parseObject(result);
        int updated = resultJO.getInteger("updated");
        return updated;
    }
}
