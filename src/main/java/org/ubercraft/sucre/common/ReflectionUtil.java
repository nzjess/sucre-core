package org.ubercraft.sucre.common;

import static org.ubercraft.sucre.common.ExceptionUtil.asUnchecked;

import java.lang.reflect.Method;

public final class ReflectionUtil {

    public static boolean isEqualsMethod(Method method) {
        return (method.getName().equals("equals") && method.getReturnType() == boolean.class //
                && method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Object.class);
    }

    public static boolean isHashCodeMethod(Method method) {
        return (method.getName().equals("hashCode") && method.getReturnType() == int.class //
        && method.getParameterTypes().length == 0);
    }

    public static boolean isToStringMethod(Method method) {
        return (method.getName().equals("toString") && method.getReturnType() == String.class //
        && method.getParameterTypes().length == 0);
    }

    public static <T> T newInstanceUnchecked(Class<? extends T> type) {
        try {
            return type.newInstance();
        }
        catch (Exception e) {
            throw asUnchecked(e);
        }
    }
}
