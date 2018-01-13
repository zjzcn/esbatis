package com.github.esbatis.spring;

import com.github.esbatis.mapper.MapperFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author jinzhong.zhang
 */
public class MapperBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;
    private MapperFactory mapperFactory;

    @Override
    public T getObject() throws Exception {
        return mapperFactory.getMapper(this.mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setMapperFactory(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
    }

}