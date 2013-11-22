package org.ubercraft.sucre.common;

import java.util.HashMap;
import java.util.Map;

public final class ObjectUtil {

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS;

    static {
        Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        map.put(boolean.class, false);
        map.put(int.class, 0);
        map.put(double.class, 0.0D);
        map.put(long.class, 0L);
        map.put(float.class, 0.0F);
        map.put(char.class, 0);
        map.put(short.class, 0);
        map.put(byte.class, 0);
        PRIMITIVE_DEFAULTS = map;

    }

    public static Object getPrimitiveDefault(Class<?> type) {
        return PRIMITIVE_DEFAULTS.get(type);
    }

    public static boolean unwrap(Boolean b, boolean nullValue) {
        return (b != null) ? b : nullValue;
    }

    public static int unwrap(Integer i, int nullValue) {
        return (i != null) ? i : nullValue;
    }

    public static long unwrap(Long l, long nullValue) {
        return (l != null) ? l : nullValue;
    }

    public static double unwrap(Double d, double nullValue) {
        return (d != null) ? d : nullValue;
    }

    public static float unwrap(Float f, float nullValue) {
        return (f != null) ? f : nullValue;
    }

    public static byte unwrap(Byte b, byte nullValue) {
        return (b != null) ? b : nullValue;
    }

    public static short unwrap(Short s, byte nullValue) {
        return (s != null) ? s : nullValue;
    }

    public static char unwrap(Character c, char nullValue) {
        return (c != null) ? c : nullValue;
    }

    public static boolean nullSafeEquals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static boolean leftEquals(Object a, Object b) {
        if (a == null || a == b) {
            return true;
        }
        if (b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static boolean rightEquals(Object a, Object b) {
        return leftEquals(b, a);
    }
}
