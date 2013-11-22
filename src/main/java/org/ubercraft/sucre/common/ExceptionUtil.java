package org.ubercraft.sucre.common;

public final class ExceptionUtil {

    public static Exception asChecked(Throwable th) throws Exception {
        if (th instanceof Exception) {
            throw (Exception)th;

        }
        throw asUnchecked(th);
    }

    public static RuntimeException asUnchecked(Throwable th) throws RuntimeException {
        if (th instanceof RuntimeException) {
            throw (RuntimeException)th;
        }
        if (th instanceof Error) {
            throw (Error)th;
        }
        throw new RuntimeException(th);
    }

    public static Error asError(Throwable th) throws Error {
        if (th instanceof Error) {
            throw (Error)th;
        }
        throw new InternalError(th.getMessage());
    }
}
