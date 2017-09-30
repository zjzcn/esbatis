package com.github.esbatis.mapper;

import com.github.esbatis.client.RestClient;
import com.github.esbatis.executor.ExecutorFilter;
import com.github.esbatis.parser.XmlMapperParser;
import com.github.esbatis.proxy.MapperProxyFactory;
import com.github.esbatis.utils.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public class MapperFactory {

    private final MapperProxyFactory mapperProxyFactory = new MapperProxyFactory(this);
    private final Map<Class<?>, Object> cachedMapperObjects = new HashMap<>();
    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    // hosts = http://ip:port,http://ip2:port2
    private RestClient restClient;

    private List<ExecutorFilter> executorFilters = new LinkedList<>();

    public RestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public MappedStatement getMappedStatement(String statement) {
        return mappedStatements.get(statement);
    }

    public synchronized <T> T getMapper(Class<T> type) {
        try {
            if (!cachedMapperObjects.containsKey(type)) {
                T t = mapperProxyFactory.newInstance(type);
                cachedMapperObjects.put(type, t);
            }
        } catch (Exception e) {
            throw new MapperException("Error getting mapper instance. Cause: " + e, e);
        }

        return (T) cachedMapperObjects.get(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getStatement(), ms);
    }

    public void addResource(String resource) {
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new MapperException(e);
        }
        addResource(inputStream);
    }

    public void addResource(InputStream inputStream) {
        XmlMapperParser mapperParser = new XmlMapperParser(inputStream, this);
        mapperParser.parse();
    }

    public void addExecutorFilter(ExecutorFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("ExecutorFilter can not null.");
        }

        executorFilters.add(filter);
    }

    public void setExecutorFilters(List<ExecutorFilter> executorFilters) {
        this.executorFilters = executorFilters;
    }

    public List<ExecutorFilter> getExecutorFilters() {
        return this.executorFilters;
    }
}
