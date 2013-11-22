package org.ubercraft.sucre.reflect;

import java.lang.annotation.Annotation;

public interface AccessorMutator {

    String getName();

    Class<?> getType();

    Class<?> getDeclaringType();

    boolean isPaired();

    Accessor getPairedAccessor();

    Mutator getPairedMutator();

    // annotation support

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    Annotation[] getAnnotations();
}
