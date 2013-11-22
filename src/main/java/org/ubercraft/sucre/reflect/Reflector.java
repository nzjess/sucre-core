package org.ubercraft.sucre.reflect;

/**
 * This interface represents a set of accessors and mutators for a class.
 */
public interface Reflector {

    // accessors

    Accessor getAccessor(String name);

    Iterable<Accessor> getAccessors();

    // mutators

    Mutator getMutator(String name);

    Iterable<Mutator> getMutators();

    // cache control

    void preCache() throws Exception;
}
