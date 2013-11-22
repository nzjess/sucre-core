package org.ubercraft.sucre.coercer;

import org.ubercraft.sucre.reflect.Accessor;

public interface CoercerFactory {

    Coercer createCoercer(Accessor accessor);
}
