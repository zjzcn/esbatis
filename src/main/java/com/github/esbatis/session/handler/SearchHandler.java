package com.github.esbatis.session.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

public class SearchHandler implements ResultHandler<Object> {

    private Class<?> returnClass;

    public SearchHandler(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    @Override
    public Object handleResult(String result) {
        JSONObject resultJO = JSON.parseObject(result);
        JSONArray hits = resultJO.getJSONObject("hits").getJSONArray("hits");
        List<Object> resultList = new ArrayList<>();
        for (Object hit : hits) {
            Object bean = ((JSONObject)hit).getObject("_source", returnClass);
            resultList.add(bean);
        }
        return resultList;
    }
}
