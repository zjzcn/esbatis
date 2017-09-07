package com.github.esbatis.proxy;

import com.github.esbatis.executor.DefaultExecutor;
import com.github.esbatis.executor.Executor;
import com.github.esbatis.core.Configuration;
import com.github.esbatis.core.MappedStatement;
import com.github.esbatis.utils.ExceptionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperProxy implements InvocationHandler {

    private static final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

    private Configuration configuration;
    private Executor executor;

    public MapperProxy(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new DefaultExecutor(configuration);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable t) {
            throw ExceptionUtils.unwrapThrowable(t);
        }
        MapperMethod mapperMethod = cachedMapperMethod(method);
        MappedStatement ms = configuration.getMappedStatement(mapperMethod.getName());
        ms.setMapperMethod(mapperMethod);

        Object result = executor.execute(ms, args);
        return result;
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * Backport of java.lang.reflect.Method#isDefault()
     */
    private boolean isDefaultMethod(Method method) {
        return ((method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC)
                && method.getDeclaringClass().isInterface();
    }

    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            mapperMethod = new MapperMethod(method);
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }
}
