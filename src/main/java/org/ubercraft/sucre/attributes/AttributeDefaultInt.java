package org.ubercraft.sucre.attributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD
})
public @interface AttributeDefaultInt {
    int value();
}
