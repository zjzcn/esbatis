package com.github.esbatis.test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeTest {

    public List<Demo> get() {
        return new ArrayList<>();
    }

    public Demo[] get1() {
        return null;
    }

    public Demo get2() {
        return null;
    }

    public List<?> get3() {
        return new ArrayList<>();
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Type type=TypeTest.class.getMethod("get").getGenericReturnType();
        System.out.println(type);
        Type type1=TypeTest.class.getMethod("get").getReturnType();
        System.out.println(type1);

        Type type2=TypeTest.class.getMethod("get1").getGenericReturnType();
        System.out.println(type2);
        Type type3=TypeTest.class.getMethod("get1").getReturnType();
        System.out.println(type3);

        Type type4=TypeTest.class.getMethod("get2").getGenericReturnType();
        System.out.println(type4);
        Type type5=TypeTest.class.getMethod("get2").getReturnType();
        System.out.println(type5);

        Type type6=TypeTest.class.getMethod("get3").getGenericReturnType();
        System.out.println(type4);
        Type type7=TypeTest.class.getMethod("get3").getReturnType();
        System.out.println(type5);
    }
}
