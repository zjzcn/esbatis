package com.github.esbatis.proxy;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.Result;
import com.github.esbatis.handler.ResultHandler;
import com.github.esbatis.mapper.MapperException;
import com.github.esbatis.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;

/**
 * @author jinzhong.zhang
 */
public class MapperMethod {

    private final String name;

    private final SortedMap<Integer, String> namedParamMap;

    private final ResultHandler resultHandler;
    private final Class<?> resultType;

    public MapperMethod(Method method) {
        final String methodName = method.getName();
        final Class<?> declaringClass = method.getDeclaringClass();
        this.name = buildName(declaringClass, methodName);
        this.namedParamMap = resolveParamMap(method);
        this.resultHandler = resolveResultHandler(method);
        this.resultType = resolveReturnType(method);
    }

    public Map<String, Object> convertArgsToParam(Object[] args) {
        Map<String, Object> param = new HashMap<>();
        for (Map.Entry<Integer, String> entry : this.namedParamMap.entrySet()) {
            param.put(entry.getValue(), args[entry.getKey()]);
        }
        return param;
    }

    public String getName() {
        return name;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public ResultHandler<?> getResultHandler() {
        return resultHandler;
    }


    private SortedMap<Integer, String> resolveParamMap(Method method) {
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<>();
        int paramCount = paramAnnotations.length;
        // get names from @Param annotations
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            String name = null;
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    name = ((Param) annotation).value();
                    break;
                }
            }
            if (name == null) {
                throw new MapperException("Method[" + this.name + "] parameter[" + paramIndex + "] must has @Param.");
            }
            map.put(paramIndex, name);
        }
        return Collections.unmodifiableSortedMap(map);
    }

    private String buildName(Class<?> mapperInterface, String methodName) {
        String name = mapperInterface.getName() + "." + methodName;
        return name;
    }

    private ResultHandler resolveResultHandler(Method method) {
        Annotation resultAnnotation = method.getDeclaredAnnotation(Result.class);
        ResultHandler handler = null;
        if (resultAnnotation != null) {
            Class<? extends ResultHandler> clazz = ((Result) resultAnnotation).value();
            handler = ClassUtils.newObject(clazz);
        }
        return handler;
    }

    private Class<?> resolveReturnType(Method method) {
        if (Map.class.isAssignableFrom(method.getReturnType())) {
            return method.getReturnType();
        }

        Type returnType = method.getGenericReturnType();
        if (returnType instanceof Class<?>) {
            return (Class<?>) returnType;
        } else if (returnType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();
            if (types.length != 1) {
                throw new ReflectionException("Method[" + this.name + "]'s returnType[getActualTypeArguments] must be length==1, but now is length==" + types.length);
            }

            if (types[0] instanceof WildcardType) {
                throw new ReflectionException("Method[" + this.name + "]'s returnType[getActualTypeArguments] can not be WildcardType");
            }

            return (Class<?>) types[0];
        } else {
            throw new ReflectionException("Method[" + this.name + "]'s returnType can not be supported");
        }
    }
}
