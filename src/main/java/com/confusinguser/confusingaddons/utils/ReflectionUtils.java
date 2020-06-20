package com.confusinguser.confusingaddons.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
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
}
