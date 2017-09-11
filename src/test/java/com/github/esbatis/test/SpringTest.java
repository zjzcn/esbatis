package com.github.esbatis.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpringTest {

    @Test
    public void test() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");

        DemoDao demoDao = applicationContext.getBean(DemoDao.class);
        Demo demo = new Demo();
        demo.setId(61L);
        demo.setAge(new ArrayList<>());
        demo.setDate(new Date());

        demoDao.index(demo);

        demoDao.update();

        boolean b = demoDao.bulk();
        System.out.println(b);

        List<Demo> list = demoDao.mget();
        System.out.println(JSON.toJSON(list));

    }

    @Test
    public void test2() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        DemoDao demoDao = applicationContext.getBean(DemoDao.class);
        for (int i=0; i<100000; i++){
            try {
                demoDao.updateByQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
