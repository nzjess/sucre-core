package org.ubercraft.sucre.reflect;

/**
 * Indicates an error occurred during a reflection operation.
 */
public class ReflectException extends RuntimeException {

    private static final long serialVersionUID = -6171811725444560176L;

    public ReflectException() {}

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(Throwable th) {
        super(th);
    }

    public ReflectException(String message, Throwable th) {
        super(message, th);
    }
}
