package com.github.esbatis.test;

import com.github.esbatis.session.ResultHandler;

public class UserResultHandler implements ResultHandler<Integer> {

    @Override
    public Integer handleResult(String result) {
        System.out.println(result);
        return 10;
    }
}
