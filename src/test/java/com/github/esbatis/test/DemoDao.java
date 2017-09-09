package com.github.esbatis.test;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.ResultType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoDao {

    Long index(@Param("demo") Demo demo);

    @ResultType(Demo.class)
    List<Demo> findDemo(@Param("id") Long id );
}
