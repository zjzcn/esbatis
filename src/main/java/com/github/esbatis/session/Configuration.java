/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.esbatis.session;

import com.github.esbatis.parser.XMLMapperParser;
import com.github.esbatis.proxy.MapperProxyFactory;
import com.github.esbatis.reflection.DefaultReflectorFactory;
import com.github.esbatis.reflection.MetaObject;
import com.github.esbatis.reflection.ReflectorFactory;
import com.github.esbatis.reflection.factory.DefaultObjectFactory;
import com.github.esbatis.reflection.factory.ObjectFactory;
import com.github.esbatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.github.esbatis.reflection.wrapper.ObjectWrapperFactory;
import com.github.esbatis.utils.ExceptionUtils;
import com.github.esbatis.utils.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Clinton Begin
 */
public class Configuration {

  private ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
  private ObjectFactory objectFactory = new DefaultObjectFactory();
  private ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

  private final MapperProxyFactory mapperProxyFactory;
  private final Set<Class<?>> mappers = new HashSet<>();
  private final Map<Class<?>, Object> cachedMapperObjects = new HashMap<>();
  private final Map<String, MappedStatement> mappedStatements = new HashMap<>();
  private final Set<String> loadedResources = new HashSet<>();
  // http://ip:port,http://ip2:port2
  private String hosts;

  public Configuration(String hosts) {
    this.mapperProxyFactory = new MapperProxyFactory();
    this.hosts = hosts;
  }

  public String getHosts() {
    return hosts;
  }

  public MappedStatement getMappedStatement(String statement) {
    return mappedStatements.get(statement);
  }

  public synchronized <T> void addMapper(Class<T> type, Session session) {
    if (!mappers.contains(type)) {
      mappers.add(type);
    }
  }

  public synchronized  <T> T getMapper(Class<T> type, Session session) {
    try {
      if (!cachedMapperObjects.containsKey(type)) {
        T t = mapperProxyFactory.newInstance(type, session);
        cachedMapperObjects.put(type, t);
      }
    } catch (Exception e) {
      throw new SessionException("Error getting mapper instance. Cause: " + e, e);
    }
    return (T)cachedMapperObjects.get(type);
  }

  public MetaObject newMetaObject(Object object) {
    return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
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
      throw ExceptionUtils.wrapException(e, SessionException.class);
    }
    XMLMapperParser mapperParser = new XMLMapperParser(inputStream, this, resource);
    mapperParser.parse();
    loadedResources.add(resource);
  }

  private boolean isResourceLoaded(String resource) {
    return loadedResources.contains(resource);
  }
}
