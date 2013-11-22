package org.ubercraft.sucre.reflect;

/**
 * Implement this interface to provide a way to create a <code>Reflector</code> instance for a class.
 */
public interface ReflectorFactory {

    Reflector createReflector(Class<?> type);
}
