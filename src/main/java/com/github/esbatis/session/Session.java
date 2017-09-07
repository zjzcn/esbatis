/**
 *    Copyright 2009-2016 the original author or authors.
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

import java.io.Closeable;

/**
 * The primary Java interface for working with MyBatis.
 * Through this interface you can execute commands, get mappers and manage transactions.
 *
 * @author Clinton Begin
 */
public interface Session extends Closeable {

  /**
   * Retrieves current configuration
   * @return Configuration
   */
  Configuration getConfiguration();

  /**
   * Retrieve a single row mapped from the statement key
   * @param <T> the returned object type
   * @param statement
   * @return Mapped object
   */
  <T> T get(String statement);

  /**
   * Retrieve a single row mapped from the statement key and parameter.
   * @param <T> the returned object type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return Mapped object
   */
  <T> T get(String statement, Object parameter);

  /**
   * Retrieve a list of mapped objects from the statement key and parameter.
   * @param statement Unique identifier matching the statement to use.
   * @return List of mapped object
   */
  <T> T search(String statement);

  /**
   * Retrieve a list of mapped objects from the statement key and parameter.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return List of mapped object
   */
  <T> T search(String statement, Object parameter);


  /**
   * Retrieve a single row mapped from the statement
   * using a {@code ResultHandler}.
   * @param statement Unique identifier matching the statement to use.
   * @param handler ResultHandler that will handle each retrieved row
   */
  <T> T search(String statement, ResultHandler<T> handler);

  /**
   * Retrieve a single row mapped from the statement key and parameter
   * using a {@code ResultHandler}.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param handler ResultHandler that will handle each retrieved row
   */
  <T> T search(String statement, Object parameter, ResultHandler<T> handler);

  /**
   * Execute an insert statement.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the insert.
   */
  <ID> ID index(String statement);

  /**
   * Execute an insert statement with the given parameter object. Any generated
   * autoincrement values or selectKey entries will modify the given parameter
   * object properties. Only the number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the insert.
   */
  <ID> ID index(String statement, Object parameter);

  /**
   * Execute an update statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the update.
   */
  int updateByQuery(String statement);

  /**
   * Execute an update statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the update.
   */
  int updateByQuery(String statement, Object parameter);

  /**
   * Execute a delete statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the delete.
   */
  boolean delete(String statement);

  /**
   * Execute a delete statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the delete.
   */
  boolean delete(String statement, Object parameter);

}
