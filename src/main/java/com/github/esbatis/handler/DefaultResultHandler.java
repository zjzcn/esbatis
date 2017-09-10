package com.github.esbatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.esbatis.mapper.MapperException;
import com.github.esbatis.mapper.CommandType;
import com.github.esbatis.mapper.MappedStatement;
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
            return null;
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
        } else if (commandType == CommandType.DELETE_BY_QUERY) {
            int deleted = resultJO.getInteger("deleted");
            return deleted;
        } else if (commandType == CommandType.UPDATE_BY_QUERY) {
            int updated = resultJO.getInteger("updated");
            return updated;
        } else if (commandType == CommandType.MGET) {
            JSONArray hits = resultJO.getJSONArray("docs");
            List<Object> resultList = new ArrayList<>();
            for (Object hit : hits) {
                Object bean = ((JSONObject)hit).getObject("_source", mapperMethod.getResultType());
                resultList.add(bean);
            }
            return resultList;
        } else if (commandType == CommandType.BULK) {
            Boolean errors = resultJO.getBoolean("errors");
            return !errors;
        } else {
            throw new HandlerException("Not supported CommandType[" + commandType + "].");
        }

    }
}
