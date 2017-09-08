package com.github.esbatis.core;

import com.github.esbatis.executor.ExecutorFilter;
import com.github.esbatis.parser.XMLMapperParser;
import com.github.esbatis.proxy.MapperProxyFactory;
import com.github.esbatis.utils.ExceptionUtils;
import com.github.esbatis.utils.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author
 */
public class Configuration {

  private final MapperProxyFactory mapperProxyFactory;
  private final Set<Class<?>> mappers = new HashSet<>();
  private final Map<Class<?>, Object> cachedMapperObjects = new HashMap<>();
  private final Map<String, MappedStatement> mappedStatements = new HashMap<>();
  private final Set<String> loadedResources = new HashSet<>();
  // http://ip:port,http://ip2:port2
  private String hosts;

  private List<ExecutorFilter> executorFilters = new LinkedList<>();

  public Configuration(String hosts) {
    this.mapperProxyFactory = new MapperProxyFactory(this);
    this.hosts = hosts;
  }

  public String getHosts() {
    return hosts;
  }

  public MappedStatement getMappedStatement(String statement) {
    return mappedStatements.get(statement);
  }

  public synchronized  <T> T getMapper(Class<T> type) {
    try {
      if (!cachedMapperObjects.containsKey(type)) {
        T t = mapperProxyFactory.newInstance(type);
        cachedMapperObjects.put(type, t);
      }
    } catch (Exception e) {
      throw new EsbatisException("Error getting mapper instance. Cause: " + e, e);
    }
    return (T)cachedMapperObjects.get(type);
  }

  public void addMappedStatement(MappedStatement ms) {
    mappedStatements.put(ms.getId(), ms);
  }

  public void addResource(String resource) {
    if (isResourceLoaded(resource)) {
      return;
    }

    InputStream inputStream = null;
    try {
      inputStream = Resources.getResourceAsStream(resource);
    } catch (IOException e) {
      throw ExceptionUtils.wrapException(e, EsbatisException.class);
    }
    XMLMapperParser mapperParser = new XMLMapperParser(inputStream, this, resource);
    mapperParser.parse();
    loadedResources.add(resource);
  }

  private boolean isResourceLoaded(String resource) {
    return loadedResources.contains(resource);
  }

  public void addExecutorFilter(ExecutorFilter filter) {
    if (filter == null) {
      throw new IllegalArgumentException("ExecutorFilter can not null.");
    }

    executorFilters.add(filter);
  }

  public List<ExecutorFilter> getExecutorFilters() {
    return this.executorFilters;
  }
}
