package com.confusinguser.confusingaddons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    private static final List<Method> methodCache = new ArrayList<>();

    @SuppressWarnings("rawtypes")
    public static Field getField(Class clazz, String... fieldNames) throws NoSuchFieldException {
        int index = 0;
        for (String fieldName : fieldNames) {
            index++;
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                if (fieldNames.length == index) { // If it's the last element
                    throw e;
                }
            }
        }
        throw new NoSuchFieldException(fieldNames[0]);
    }

    @SuppressWarnings("rawtypes")
    public static Method getMethod(Class clazz, String... methodNames) throws NoSuchMethodException {
        int index = 0;

        for (Method method : methodCache) {
            for (String methodName : methodNames) {
                if (method.getName().equals(methodName) && method.getDeclaringClass() == clazz) {
                    return method;
                }
            }
        }

        for (String methodName : methodNames) {
            index++;
            try {
                Method method = clazz.getDeclaredMethod(methodName);
                method.setAccessible(true);
                methodCache.add(method);
                return method;
            } catch (NoSuchMethodException e) {
                if (methodNames.length == index) { // If it's the last element
                    throw e;
                }
            }
        }
        throw new NoSuchMethodException(methodNames[0]);
    }
}
