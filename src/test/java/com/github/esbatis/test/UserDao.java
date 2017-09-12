package com.github.esbatis.test;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.Result;

import java.util.List;

public interface UserDao {

    @Result(UserResultHandler.class)
    Integer avgUser(@Param("index") String index, @Param("type") String type,
                    @Param("list") List<String> list, @Param("user") User user);

    List<User> avgUser1(@Param("user") User user);

    List<User> avgUser2(@Param("name") String name);

    Long index(@Param("user") User user);

    User getById(@Param("id") Long id);
}
