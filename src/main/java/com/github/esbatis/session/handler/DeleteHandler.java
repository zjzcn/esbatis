package com.github.esbatis.session.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.session.ResultHandler;

public class DeleteHandler implements ResultHandler<Boolean> {

    private Class<?> returnClass;

    public DeleteHandler(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    @Override
    public Boolean handleResult(String result) {
        JSONObject resultJO = JSON.parseObject(result);
        Boolean found = resultJO.getBoolean("found");
        return found;
    }
}
