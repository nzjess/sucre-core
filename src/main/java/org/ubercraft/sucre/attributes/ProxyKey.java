package org.ubercraft.sucre.attributes;

import java.io.Serializable;

import org.ubercraft.sucre.coercer.Coercer;
import org.ubercraft.sucre.coercer.CoercerUtil;
import org.ubercraft.sucre.reflect.Accessor;

public final class ProxyKey implements Serializable {

    private static final long serialVersionUID = 2332758839196022712L;

    private final Accessor accessor;
    private final Object[] attributeKeys;
    private final Object defaultValue;
    private final Coercer coercer;
    private final boolean isStrictCoerce;

    public ProxyKey(Accessor accessor, Object attributeKey) {
        this(accessor, attributeKey, null, null, false);
    }

    public ProxyKey(Accessor accessor, Object attributeKey, Object defaultValue) {
        this(accessor, attributeKey, defaultValue, null, false);
    }

    public ProxyKey(Accessor accessor, Object attributeKey, Object defaultValue, boolean isStrictCoerce) {
        this(accessor, attributeKey, defaultValue, null, isStrictCoerce);
    }

    public ProxyKey(Accessor accessor, Object attributeKey, boolean isStrictCoerce) {
        this(accessor, attributeKey, null, null, isStrictCoerce);
    }

    public ProxyKey(Accessor accessor, Object attributeKey, Coercer coercer, boolean isStrictCoerce) {
        this(accessor, attributeKey, null, coercer, isStrictCoerce);
    }

    public ProxyKey(Accessor accessor, Object attributeKey, Object defaultValue, Coercer coercer, boolean isStrictCoerce) {
        this.accessor = accessor;
        if (attributeKey == null || !attributeKey.getClass().isArray()) {
            attributeKey = new Object[] {
                attributeKey
            };
        }
        this.attributeKeys = (Object[])attributeKey;
        if (this.attributeKeys.length == 0) {
            throw new IllegalArgumentException("must have at least one attribute key");
        }
        this.defaultValue = defaultValue;
        if (coercer == null) {
            coercer = CoercerUtil.DEFAULT_COERCER;
        }
        this.coercer = coercer;
        this.isStrictCoerce = isStrictCoerce;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    // package access only
    Object[] getAttributeKeys() {
        return attributeKeys;
    }

    public int getNumKeys() {
        return attributeKeys.length;
    }

    public Object getKey(int index) {
        return attributeKeys[index];
    }

    public Object getDefault() {
        return defaultValue;
    }

    public Coercer getCoercer() {
        return coercer;
    }

    public boolean isStrictCoerce() {
        return isStrictCoerce;
    }
}
