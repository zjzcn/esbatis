package com.github.esbatis.spring;

import com.github.esbatis.client.RestClient;
import com.github.esbatis.executor.ExecutorFilter;
import com.github.esbatis.mapper.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author jinzhong.zhang
 */
public class MapperFactoryBean implements FactoryBean<MapperFactory>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MapperFactoryBean.class);

    private MapperFactory mapperFactory;
    private Resource[] mapperLocations;
    private RestClient restClient;
    private List<ExecutorFilter> executorFilters;

    @Override
    public void afterPropertiesSet() throws Exception {
        mapperFactory = new MapperFactory();
        mapperFactory.setRestClient(restClient);
        mapperFactory.setExecutorFilters(executorFilters);
        addResources();
    }

    @Override
    public MapperFactory getObject() throws Exception {
        if (this.mapperFactory == null) {
            afterPropertiesSet();
        }

        return this.mapperFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public void setExecutorFilters(List<ExecutorFilter> executorFilters) {
        this.executorFilters = executorFilters;
    }

    private void addResources() throws NestedIOException {
        if (!isEmpty(this.mapperLocations)) {
            for (Resource mapperLocation : this.mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }

                try {
                    mapperFactory.addResource(mapperLocation.getInputStream());
                } catch (Exception e) {
                    throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                }

                logger.info("Parsed mapper file: '" + mapperLocation + "'");
            }
        } else {
            logger.info("Property 'mapperLocations' was not specified or no matching resources found");
        }
    }

    private static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
}