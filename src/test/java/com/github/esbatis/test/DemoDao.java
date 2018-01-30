package com.github.esbatis.test;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.Result;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoDao {

    Long index(@Param("demo") Demo demo);

    List<Demo> findDemo(@Param("id") Long id );

    void update();

    boolean bulk();

    List<Demo> mget();

    int updateByQuery();

    Long insertPolygon();

    @Result(DemoResultHandler.class)
    Integer avg(@Param("demo") Demo demo);
}
