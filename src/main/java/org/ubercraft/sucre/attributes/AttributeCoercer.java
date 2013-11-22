package org.ubercraft.sucre.attributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ubercraft.sucre.coercer.CoercerFactory;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD
})
public @interface AttributeCoercer {
    Class<? extends CoercerFactory> value();
}
