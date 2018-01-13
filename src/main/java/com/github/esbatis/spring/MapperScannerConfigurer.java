package com.github.esbatis.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;

import static org.springframework.util.Assert.notNull;

/**
 * @author jinzhong.zhang
 */
public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware {

    private String mapperFactoryBeanId;

    private String basePackage;
    private Class<? extends Annotation> annotationClass;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // left intentionally blank
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        scanner.setMapperFactoryBeanId(this.mapperFactoryBeanId);
        scanner.setAnnotationClass(this.annotationClass);
        scanner.setResourceLoader(this.applicationContext);
        scanner.registerFilters();
        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMapperFactoryBeanId(String mapperFactoryBeanId) {
        this.mapperFactoryBeanId = mapperFactoryBeanId;
    }

}
