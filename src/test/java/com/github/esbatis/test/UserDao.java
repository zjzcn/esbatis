package com.github.esbatis.test;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.ResultHandlerType;
import com.github.esbatis.annotations.ResultType;

import java.util.List;

public interface UserDao {

    @ResultHandlerType(UserResultHandler.class)
    Integer avgUser(@Param("index") String index, @Param("type") String type,
                    @Param("list") List<String> list, @Param("user") User user);

    Integer avgUser1(User user);

    @ResultType(User.class)
    List<User> avgUser2(@Param("name") String name);

    Long index(User user);

    User getById(@Param("id") Long id);
}
