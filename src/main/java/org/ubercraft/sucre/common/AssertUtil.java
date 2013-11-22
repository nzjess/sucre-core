package org.ubercraft.sucre.common;

public final class AssertUtil {

    public static <T> T isNull(T o) {
        return isNull(o, null);
    }

    public static <T> T isNull(T o, String message) {
        if (o != null) {
            throw new IllegalStateException(message);
        }
        return o;
    }

    public static <T> T notNull(T o) {
        return notNull(o, null);
    }

    public static <T> T notNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }
}
