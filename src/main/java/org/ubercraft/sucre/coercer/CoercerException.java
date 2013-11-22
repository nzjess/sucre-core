package org.ubercraft.sucre.coercer;

public class CoercerException extends RuntimeException {

    private static final long serialVersionUID = -1492333694959861169L;

    public CoercerException() {}

    public CoercerException(String message) {
        super(message);
    }

    public CoercerException(Throwable cause) {
        super(cause);
    }

    public CoercerException(String message, Throwable cause) {
        super(message, cause);
    }
}
