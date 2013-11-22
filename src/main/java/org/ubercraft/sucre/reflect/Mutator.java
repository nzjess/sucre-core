package org.ubercraft.sucre.reflect;

public interface Mutator extends AccessorMutator {

    void mutate(Object object, Object value);
}
