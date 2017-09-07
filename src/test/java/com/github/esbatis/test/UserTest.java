package com.github.esbatis.test;

import com.alibaba.fastjson.JSON;
import com.github.esbatis.session.Configuration;
import com.github.esbatis.session.DefaultSessionFactory;
import com.github.esbatis.session.Session;
import com.github.esbatis.session.SessionFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserTest {

    @Test
    public void test() {
        Configuration configuration = new Configuration("http://10.101.91.60:9200/");
        configuration.addResource("UserDao.xml");

        SessionFactory sessionFactory = new DefaultSessionFactory(configuration);
        Session session = sessionFactory.openSession();
        UserDao userDao = configuration.getMapper(UserDao.class, session);
        List<String> list = new ArrayList<>();
        list.add("xxx");
        list.add("ffff");
        list.add("iiii");
        User user = new User();
        user.setName("gmv");
        Integer i = userDao.avgUser("bdi_eco_odgraph_20170809","shop_user", list, user);
        System.out.println(i);

        user.setName("rrrrrrr");
        userDao.avgUser1(user);
    }


    @Test
    public void test1() {
        Configuration configuration = new Configuration("http://10.101.91.60:9200");
        configuration.addResource("UserDao.xml");

        SessionFactory sessionFactory = new DefaultSessionFactory(configuration);
        Session session = sessionFactory.openSession();
        UserDao userDao = configuration.getMapper(UserDao.class, session);
        User user = new User();
        user.setName("xxx");
        userDao.avgUser1(user);
    }

    @Test
    public void test2() {
        Configuration configuration = new Configuration("http://10.101.91.60:9200");
        configuration.addResource("UserDao.xml");

        SessionFactory sessionFactory = new DefaultSessionFactory(configuration);
        Session session = sessionFactory.openSession();
        UserDao userDao = configuration.getMapper(UserDao.class, session);
        List<User> list = userDao.avgUser2("gmv");
        System.out.println(JSON.toJSON(list));
    }

    @Test
    public void test3() {
        Configuration configuration = new Configuration("http://10.101.91.60:9200");
        configuration.addResource("UserDao.xml");

        SessionFactory sessionFactory = new DefaultSessionFactory(configuration);
        Session session = sessionFactory.openSession();
        UserDao userDao = configuration.getMapper(UserDao.class, session);
        User user = new User();
        user.setName("rrrrrrrrr");
        user.setId(100000000L);
        Long id = userDao.index(user);
        System.out.println(id);
    }

    @Test
    public void test4() {
        Configuration configuration = new Configuration("http://10.101.91.60:9200");
        configuration.addResource("UserDao.xml");

        SessionFactory sessionFactory = new DefaultSessionFactory(configuration);
        Session session = sessionFactory.openSession();
        UserDao userDao = configuration.getMapper(UserDao.class, session);
        User user = userDao.getById(100000000L);
        System.out.println(JSON.toJSON(user));
    }
}
