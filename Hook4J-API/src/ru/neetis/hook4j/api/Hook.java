package ru.neetis.hook4j.api;

import java.lang.reflect.Method;

public class Hook {
    private final Class<?> clazz;
    private final Method method;
    private final HookType hookType;
    private final String source;

    public Hook(final Method method, final HookType type, final String hook) {
        this.clazz = method.getDeclaringClass();
        this.method = method;
        this.hookType = type;
        this.source = hook;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    public HookType getHookType() {
        return hookType;
    }

    public String getSource() {
        return source;
    }
}
