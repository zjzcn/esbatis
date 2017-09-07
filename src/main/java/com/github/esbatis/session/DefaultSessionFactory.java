package com.github.esbatis.session;

/**
 * @author
 */
public class DefaultSessionFactory implements SessionFactory {

  private final Configuration configuration;

  public DefaultSessionFactory(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Session openSession() {
    return new DefaultSession(configuration);
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

}
