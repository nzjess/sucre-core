package org.ubercraft.sucre.attributes;

import static org.ubercraft.sucre.coercer.CoercerUtil.coerceToBoolean;
import static org.ubercraft.sucre.coercer.CoercerUtil.coerceToDouble;
import static org.ubercraft.sucre.coercer.CoercerUtil.coerceToInteger;
import static org.ubercraft.sucre.coercer.CoercerUtil.coerceToString;
import static org.ubercraft.sucre.common.AssertUtil.notNull;
import static org.ubercraft.sucre.common.ObjectUtil.unwrap;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ubercraft.sucre.common.ObjectUtil;
import org.ubercraft.sucre.common.ReflectionUtil;
import org.ubercraft.sucre.reflect.Accessor;
import org.ubercraft.sucre.reflect.MethodReflector;
import org.ubercraft.sucre.reflect.Mutator;
import org.ubercraft.sucre.reflect.Reflect;
import org.ubercraft.sucre.reflect.Reflector;

public final class Attributes implements Serializable {

    private static final long serialVersionUID = 6687989846839179831L;

    public static final Attributes EMPTY_ATTRIBUTES = new Attributes(Collections.emptyMap());

    public static Attributes backedBy(Map<? extends Object, ? extends Object> values) {
        return backedBy(values, null);
    }

    public static Attributes backedBy(Map<? extends Object, ? extends Object> values, Attributes defaults) {
        return new Attributes(notNull(values), defaults, true);
    }

    private final Map<Object, Object> values;

    private final Attributes defaults;

    public Attributes() {
        this(null, null);
    }

    public Attributes(Map<? extends Object, ? extends Object> values) {
        this(values, null);
    }

    public Attributes(Attributes defaults) {
        this(null, defaults);
    }

    public Attributes(Map<? extends Object, ? extends Object> values, Attributes defaults) {
        this(values, defaults, false);
    }

    @SuppressWarnings("unchecked")
    private Attributes(Map<? extends Object, ? extends Object> values, Attributes defaults, boolean backed) {
        this.values = (Map<Object, Object>)((values != null) ? (backed ? values : new HashMap<Object, Object>(values)) : new HashMap<Object, Object>());
        this.defaults = defaults;
    }

    public Map<Object, Object> getValues() {
        return values;
    }

    public Attributes getDefaults() {
        return defaults;
    }

    public Set<Object> keySet() {
        return values.keySet();
    }

    public boolean hasKey(Object key) {
        return values.containsKey(key);
    }

    public boolean hasKeyOrDefaultKey(Object key) {
        return hasKey(key) || (defaults != null && defaults.hasKeyOrDefaultKey(key));
    }

    public Object get(Object key) {
        return get(key, null);
    }

    public Object get(Object key, Object def) {
        return hasKey(key) ? values.get(key) : (defaults != null) ? defaults.get(key, def) : def;
    }

    public String getString(Object key) {
        return getString(key, null);
    }

    public String getString(Object key, String def) {
        return coerceToString(get(key, def));
    }

    public Boolean getBoolean(Object key) {
        return coerceToBoolean(get(key), false);
    }

    public boolean getBoolean(Object key, boolean def) {
        return unwrap(getBoolean(key), def);
    }

    public Integer getInt(Object key) {
        return coerceToInteger(get(key), false);
    }

    public int getInt(Object key, int def) {
        return unwrap(getInt(key), def);
    }

    public Double getDouble(Object key) {
        return coerceToDouble(get(key), false);
    }

    public double getDouble(Object key, double def) {
        return unwrap(getDouble(key), def);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTyped(Class<T> type) {
        return (T)get(type);
    }

    public Object set(Object key, Object value) {
        return values.put(key, value);
    }

    public Object remove(Object key) {
        return values.remove(key);
    }

    public void addAll(Attributes attributes) {
        this.values.putAll(attributes.values);
    }

    public void addAll(Map<? extends Object, ? extends Object> values) {
        this.values.putAll(values);
    }

    public void clear() {
        values.clear();
    }

    @Override
    public int hashCode() {
        int hashCode = values.hashCode();
        if (defaults != null) {
            return hashCode ^= defaults.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Attributes other = (Attributes)o;
        return (this.values.equals(other.values)) && //
                ObjectUtil.nullSafeEquals(this.defaults, other.defaults);
    }

    @Override
    public String toString() {
        return "values: " + values + ((defaults != null) ? ", default " + defaults : "");
    }

    /*
     * Proxy support follows...
     */

    public <T> T proxy(Class<T> api, Class<?>... apis) {
        return proxy(null, null, api, apis);
    }

    public <T> T proxy(Reflect reflect, Class<T> api, Class<?>... apis) {
        return proxy(null, reflect, api, apis);
    }

    public <T> T proxy(ProxyKeyFactory factory, Class<T> api, Class<?>... apis) {
        return proxy(factory, null, api, apis);
    }

    @SuppressWarnings("unchecked")
    public <T> T proxy(ProxyKeyFactory factory, Reflect reflect, Class<T> api, Class<?>... apis) {
        Class<?>[] interfaces = new Class[1 + ((apis != null) ? apis.length : 0)];
        interfaces[0] = api;
        if (apis != null) {
            System.arraycopy(apis, 0, interfaces, 1, apis.length);
        }
        return (T)proxy(factory, reflect, interfaces);
    }

    private Object proxy(ProxyKeyFactory factory, Reflect reflect, Class<?>... apis) {
        if (factory == null) {
            factory = DefaultProxyKeyFactory.DEFAULT_PROXY_KEY_FACTORY;
        }
        Map<Method, ProxyKey> mappings = new HashMap<Method, ProxyKey>();
        for (Class<?> api : apis) {
            createProxyKeyMappings(factory, reflect, api, mappings);
        }
        return Proxy.newProxyInstance(apis[0].getClassLoader(), apis, //
                new AttributeProxyInvocationHandler(mappings));
    }

    private static void createProxyKeyMappings(ProxyKeyFactory factory, Reflect reflect, Class<?> api, Map<Method, ProxyKey> mappings) {
        if (reflect == null) {
            reflect = Reflect.METHODS;
        }
        Reflector reflector = reflect.getReflector(api);
        for (Accessor accessor : reflector.getAccessors()) {
            Method accessorMethod = ((MethodReflector)accessor).getMethod();
            if (!mappings.containsKey(accessorMethod)) {
                ProxyKey proxyKey = factory.proxyKeyFor(accessor);
                if (proxyKey != null) {
                    mappings.put(accessorMethod, proxyKey);
                    if (accessor.isPaired()) {
                        Mutator mutator = reflector.getMutator(accessor.getName());
                        Method mutatorMethod = ((MethodReflector)mutator).getMethod();
                        if (!mappings.containsKey(mutatorMethod)) {
                            mappings.put(mutatorMethod, proxyKey);
                        }
                    }
                }
            }
        }
    }

    private class AttributeProxyInvocationHandler implements InvocationHandler {

        private final Map<Method, ProxyKey> mappings;

        AttributeProxyInvocationHandler(Map<Method, ProxyKey> mappings) {
            this.mappings = Collections.unmodifiableMap(mappings);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ProxyKey key = mappings.get(method);
            Class<?> type = method.getReturnType();
            if (key != null) {
                if (type == void.class) {
                    setValue(args, key);
                    return null;
                }
                else {
                    return getValue(key, type);
                }
            }
            else if (Attributes.class.isAssignableFrom(type) && method.getParameterTypes().length == 0) {
                return Attributes.this;
            }
            else if (ReflectionUtil.isHashCodeMethod(method)) {
                return System.identityHashCode(proxy);
            }
            else if (ReflectionUtil.isEqualsMethod(method)) {
                return (proxy == args[0]);
            }
            else if (ReflectionUtil.isToStringMethod(method)) {
                return toString();
            }
            else if (method.getReturnType().isPrimitive()) {
                return ObjectUtil.getPrimitiveDefault(method.getReturnType());
            }
            else {
                return null;
            }
        }

        private Object getValue(ProxyKey key, Class<?> type) {
            Object value = key.getDefault();
            for (Object attributeKey : key.getAttributeKeys()) {
                if (hasKeyOrDefaultKey(attributeKey)) {
                    value = get(attributeKey, value);
                    break;
                }
            }
            if (value != null) {
                value = key.getCoercer().coerce(value, type, key.isStrictCoerce());
            }
            if (value == null && type.isPrimitive()) {
                value = ObjectUtil.getPrimitiveDefault(type);
            }
            return value;
        }

        private void setValue(Object[] args, ProxyKey key) {
            Object value = args[0];
            for (Object attributeKey : key.getAttributeKeys()) {
                set(attributeKey, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("[");
            for (ProxyKey key : mappings.values()) {
                b.append(Arrays.asList(key.getAttributeKeys()));
                b.append("=");
                b.append(getValue(key, Object.class));
                b.append(", ");
            }
            if (b.length() > 1) {
                b.setLength(b.length() - 2);
            }
            b.append("]");
            return b.toString();
        }

        private Attributes getAttributes() {
            return Attributes.this;
        }
    }

    private static AttributeProxyInvocationHandler getInvocationHandler(Object proxy) {
        return (AttributeProxyInvocationHandler)Proxy //
                .getInvocationHandler(proxy);
    }

    public static Attributes getProxiedAttributes(Object proxy) {
        return getInvocationHandler(proxy).getAttributes();
    }

    public static Collection<ProxyKey> getProxyKeys(Object proxy) {
        return getInvocationHandler(proxy).mappings.values();
    }
}
