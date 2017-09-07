package com.github.esbatis.test;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.ResultType;

import java.util.List;

public interface DemoDao {

    Long index(Demo demo);

    @ResultType(Demo.class)
    List<Demo> findDemo(@Param("dataDt") String dataDt,
                        @Param("checkType") Integer checkType,
                        @Param("checkId") Long checkId);
}
