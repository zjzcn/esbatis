package com.github.esbatis.test;

import com.github.esbatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

public class UserResultHandler1 implements ResultHandler<List<User>> {

    @Override
    public List<User> handleResult(String result) {
        User user = new User();
        user.setName("zjz");
        List<User> list = new ArrayList<>();
        list.add(user);
        return list;
    }
}
