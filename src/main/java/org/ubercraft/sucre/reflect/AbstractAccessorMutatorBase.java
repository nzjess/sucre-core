package org.ubercraft.sucre.reflect;

abstract class AbstractAccessorMutatorBase implements AccessorMutator {

    private final String name;
    private final Class<?> type;

    AbstractAccessorMutatorBase(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return getDeclaringType() + "." + getName();
    }
}
