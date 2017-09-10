package com.github.esbatis.spring;

import com.github.esbatis.mapper.MapperFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author jinzhong.zhang
 * @param <T>
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;
    private MapperFactory mapperFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public T getObject() throws Exception {
        return mapperFactory.getMapper(this.mapperInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    //------------- mutators --------------

    /**
     * Sets the mapper interface of the MyBatis mapper
     *
     * @param mapperInterface class of the interface
     */
    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * Return the mapper interface of the MyBatis mapper
     *
     * @return class of the interface
     */
    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperFactory(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
    }
}