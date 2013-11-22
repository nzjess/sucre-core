package org.ubercraft.sucre.coercer;

public interface Coercer {

    Object coerce(Object value, Class<?> type, boolean strict);
}
