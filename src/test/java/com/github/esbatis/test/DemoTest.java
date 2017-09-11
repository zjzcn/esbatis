package com.github.esbatis.test;

import com.alibaba.fastjson.JSON;
import com.github.esbatis.mapper.MapperFactory;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class DemoTest {

    @Test
    public void test() {
        MapperFactory mapperFactory = new MapperFactory();
        mapperFactory.setHttpHosts("http://10.101.91.60:9200/");
        mapperFactory.addResource("mapper/DemoDao.xml");

        DemoDao demoDao = mapperFactory.getMapper(DemoDao.class);
        Demo demo = new Demo();
        demo.setId(3L);
        demo.setDate(new Date());

        demoDao.index(demo);
    }

    @Test
    public void test2() {
        MapperFactory configuration = new MapperFactory();
        configuration.setHttpHosts("http://10.101.91.60:9200/");
        configuration.addResource("mapper/DemoDao.xml");

        DemoDao userDao = configuration.getMapper(DemoDao.class);
        List<Demo> list = userDao.findDemo(1000000002L);
        System.out.println(JSON.toJSON(list));
    }
}
