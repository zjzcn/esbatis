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

import com.github.esbatis.client.RestClient;
import com.github.esbatis.session.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * The default implementation for {@link Session}.
 * Note that this class is not Thread-Safe.
 *
 * @author Clinton Begin
 */
public class DefaultSession implements Session {

  private static final Logger logger = LoggerFactory.getLogger(DefaultSession.class);

  private final Configuration configuration;
  private final RestClient restClient;
  public DefaultSession(Configuration configuration) {
    this.configuration = configuration;
    this.restClient = new RestClient(configuration.getHosts());
  }

  @Override
  public void close() {
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public <T> T get(String statement) {
    return get(statement, null);
  }

  @Override
  public <T> T get(String statement, Object parameter) {
    MappedStatement ms = this.configuration.getMappedStatement(statement);

    String resp = sendHttpRequest(ms, parameter);

    Class<?> clazz = ms.getMapperMethod().getReturnClass();
    ResultHandler<?> handler = new GetHandler(clazz);
    return (T)handler.handleResult(resp);
  }

  @Override
  public <T> T search(String statement) {
    return search(statement, null, null);
  }

  @Override
  public <T> T search(String statement, Object parameter) {
    return search(statement, parameter, null);
  }

  @Override
  public <T> T search(String statement, ResultHandler<T> handler) {
    return search(statement, null, handler);
  }

  @Override
  public <T> T search(String statement, Object parameter, ResultHandler<T> handler) {
    MappedStatement ms = this.configuration.getMappedStatement(statement);

    String resp = sendHttpRequest(ms, parameter);

    Class<?> clazz = ms.getMapperMethod().getResultType();
    if (handler == null) {
      ResultHandler<?> defaultHandler = new SearchHandler(clazz);
      return (T)defaultHandler.handleResult(resp);
    } else {
      return handler.handleResult(resp);
    }
  }

  @Override
  public <T> T index(String statement) {
   return index(statement, null);
  }

  @Override
  public <T> T index(String statement, Object parameter) {
    MappedStatement ms = this.configuration.getMappedStatement(statement);
    Class<?> clazz = ms.getMapperMethod().getReturnClass();
    String resp = sendHttpRequest(ms, parameter);
    ResultHandler<?> handler = new IndexHandler(clazz);
    return (T)handler.handleResult(resp);
  }

  @Override
  public int updateByQuery(String statement) {
    return updateByQuery(statement, null);
  }

  @Override
  public int updateByQuery(String statement, Object parameter) {
    MappedStatement ms = this.configuration.getMappedStatement(statement);
    Class<?> clazz = ms.getMapperMethod().getReturnClass();
    String resp = sendHttpRequest(ms, parameter);
    ResultHandler<?> handler = new UpdateHandler(clazz);
    return (Integer) handler.handleResult(resp);
  }

  @Override
  public boolean delete(String statement) {
    return delete(statement, null);
  }

  @Override
  public boolean delete(String statement, Object parameter) {
    MappedStatement ms = this.configuration.getMappedStatement(statement);
    Class<?> clazz = ms.getMapperMethod().getReturnClass();
    String resp = sendHttpRequest(ms, parameter);
    ResultHandler<?> handler = new DeleteHandler(clazz);
    return (Boolean) handler.handleResult(resp);
  }

  private String sendHttpRequest(MappedStatement ms, Object parameterObject) {
    BoundHttp http = ms.getBoundHttp(parameterObject);
    String resp = restClient.send(http.url(), http.method(), http.body());
    return resp;
  }

}
