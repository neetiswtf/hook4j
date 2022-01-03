package ru.neetis.hook4j.utils;

import java.lang.reflect.Method;

public class ReflectionUtils {
    /*
        This method used for getting method from caller class.
     */
    public static Method getMethodFromCaller(final String method, final Class<?>... parameters) {
        final StackTraceElement[] stElements = Thread.currentThread().getStackTrace(); // I've pasted this shit from SO lol
        final String rawFQN = stElements[2].toString().split("\\(")[0];
        Class<?> clazz = null;
        try {
            clazz = Class.forName(rawFQN.substring(0, rawFQN.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return clazz.getDeclaredMethod(method, parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /*
        This method used for getting method from any class
     */

    public static Method getMethod(final Class<?> clazz, final String method, final Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(method, parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
