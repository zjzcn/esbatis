package com.github.esbatis.config;

import com.github.esbatis.exceptions.EsbatisException;
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
  // http://ip:port,http://ip2:port2
  private String hosts;

  public Configuration() {
    this.mapperProxyFactory = new MapperProxyFactory(this);
  }

  private List<ExecutorFilter> executorFilters = new LinkedList<>();

  public String getHosts() {
    return hosts;
  }

  public void setHosts(String hosts) {
    this.hosts = hosts;
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
    InputStream inputStream = null;
    try {
      inputStream = Resources.getResourceAsStream(resource);
    } catch (IOException e) {
      throw ExceptionUtils.wrapException(e, EsbatisException.class);
    }
    XMLMapperParser mapperParser = new XMLMapperParser(inputStream, this);
    mapperParser.parse();
  }

  public void addResource(InputStream inputStream) {
    XMLMapperParser mapperParser = new XMLMapperParser(inputStream, this);
    mapperParser.parse();
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