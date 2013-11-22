package org.ubercraft.sucre.reflect;

import static org.ubercraft.sucre.common.AssertUtil.notNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main entry point to get and/or register reflector factories and reflectors by type.
 */
public class Reflect {

    /**
     * A shared instance for method-based access.
     */
    public static final Reflect METHODS = new Reflect(MethodReflectorFactory.class);

    /**
     * A shared instance for field-based access.
     */
    public static final Reflect FIELDS = new Reflect(FieldReflectorFactory.class);

    // default reflector factory class, for when there is no specific factory set for a given type.
    private final Class<? extends ReflectorFactory> defaultFactoryClass;

    // maps type to reflector factory type
    private final Map<Class<?>, Class<? extends ReflectorFactory>> factoryMappings = new ConcurrentHashMap<Class<?>, Class<? extends ReflectorFactory>>();

    // caches reflector factory instances per type
    private final Map<Class<?>, ReflectorFactory> factories = new ConcurrentHashMap<Class<?>, ReflectorFactory>();

    // caches reflector instances per type
    private final Map<Class<?>, Reflector> reflectorCache = new ConcurrentHashMap<Class<?>, Reflector>();

    /**
     * Create a new instance, defaulting to the built-in method reflection factory.
     */
    public Reflect() {
        this(MethodReflectorFactory.class);
    }

    /**
     * Create a new instance with the given default reflector factory type.
     */
    public Reflect(Class<? extends ReflectorFactory> defaultFactoryClass) {
        this.defaultFactoryClass = defaultFactoryClass;
    }

    /**
     * Set reflector factory class for a type (pass null factory class to unset).
     */
    public void setReflectorFactoryClass(Class<?> type, Class<? extends ReflectorFactory> clazz) {
        notNull(type, "type cannot be null");

        if (type != null) {
            factoryMappings.put(type, clazz);
        }
        else {
            factoryMappings.remove(type);
        }
    }

    /**
     * Causes the reflector for the given type to be pre-cached.
     */
    public void preCache(Class<?> type) {
        notNull(type, "type cannot be null");

        try {
            Reflector reflector = getReflector(type);
            reflector.preCache();
        }
        catch (ReflectException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ReflectException("failed to pre-cache accessor/mutator maps for type: " + type.getName(), e);
        }
    }

    /**
     * Removes any cached reflector for the given type.
     */
    public void unCache(Class<?> type) {
        reflectorCache.remove(type);
    }

    /**
     * Completely clears the reflector cache.
     */
    public void clearCache() {
        reflectorCache.clear();
    }

    /**
     * Gets the reflector for a type. Hits the cache first to avoid re-creating the reflector everytime.
     */
    public Reflector getReflector(Class<?> type) {
        notNull(type, "type cannot be null");

        try {
            // check cache
            Reflector reflector = (Reflector)reflectorCache.get(type);
            if (reflector != null) {
                return reflector;
            }

            // look up factory
            ReflectorFactory factory = (ReflectorFactory)factories.get(type);
            if (factory == null) {
                // get factory class
                Class<?> clazz = (Class<?>)factoryMappings.get(type);
                if (clazz == null) {
                    clazz = defaultFactoryClass;
                }

                // create and cache factory
                factory = (ReflectorFactory)clazz.newInstance();
                factories.put(type, factory);
            }

            // create and cache reflector
            reflector = factory.createReflector(type);
            reflectorCache.put(type, reflector);

            // return reflector
            return reflector;
        }
        catch (ReflectException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ReflectException("failed to create reflector for type: " + type.getName(), e);
        }
    }
}
