package com.github.esbatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.core.EsbatisException;
import com.github.esbatis.core.CommandType;
import com.github.esbatis.core.MappedStatement;
import com.github.esbatis.proxy.MapperMethod;

import java.util.ArrayList;
import java.util.List;

public class DefaultResultHandler implements ResultHandler<Object> {

    private MappedStatement statement;

    public DefaultResultHandler(MappedStatement statement) {
        this.statement = statement;
    }

    @Override
    public Object handleResult(String result) {
        JSONObject resultJO = JSON.parseObject(result);
        MapperMethod mapperMethod = statement.getMapperMethod();
        CommandType commandType = statement.getCommandType();

        if (commandType == CommandType.SEARCH) {
            JSONArray hits = resultJO.getJSONObject("hits").getJSONArray("hits");
            List<Object> resultList = new ArrayList<>();
            for (Object hit : hits) {
                Object bean = ((JSONObject)hit).getObject("_source", mapperMethod.getResultType());
                resultList.add(bean);
            }
            return resultList;
        } else if (commandType == CommandType.GET) {
            Object bean = resultJO.getObject("_source", mapperMethod.getReturnClass());
            return bean;
        } else if (commandType == CommandType.DELETE) {
            Boolean found = resultJO.getBoolean("found");
            return found;
        } else if (commandType == CommandType.UPDATE) {
            int updated = resultJO.getInteger("updated");
            return updated;
        } else if (commandType == CommandType.INDEX) {
            Class<?> returnClass = mapperMethod.getReturnClass();
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
        } else {
            throw new EsbatisException("Not supported CommandType[" + commandType + "].");
        }

    }
}
