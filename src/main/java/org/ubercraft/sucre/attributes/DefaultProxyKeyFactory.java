package org.ubercraft.sucre.attributes;

import static org.ubercraft.sucre.attributes.AttributesUtil.attributeKeyFor;
import static org.ubercraft.sucre.attributes.AttributesUtil.coercerFor;
import static org.ubercraft.sucre.attributes.AttributesUtil.defaultValueFor;
import static org.ubercraft.sucre.attributes.AttributesUtil.isCoerceStrictFor;

import org.ubercraft.sucre.coercer.Coercer;
import org.ubercraft.sucre.reflect.Accessor;

public class DefaultProxyKeyFactory implements ProxyKeyFactory {

    public static final ProxyKeyFactory DEFAULT_PROXY_KEY_FACTORY = new DefaultProxyKeyFactory();

    @Override
    public ProxyKey proxyKeyFor(Accessor accessor) {
        return createProxyKey(accessor, null, null);
    }

    protected ProxyKey proxyKeyWithAttributeKeyFor(Accessor accessor, Object attributeKey) {
        return createProxyKey(accessor, attributeKey, false);
    }

    protected ProxyKey proxyKeyWithAttributeKeysFor(Accessor accessor, Object... attributeKeys) {
        return createProxyKey(accessor, attributeKeys, false);
    }

    private ProxyKey createProxyKey(Accessor accessor, Object object, Boolean whichWay) {
        Object attributeKey = (whichWay == null || whichWay) ? getAttributeKeyFor(accessor) : object;
        Object defaultValue = (whichWay == null || !whichWay) ? getDefaultValueFor(accessor) : object;
        Coercer coercer = getCoercerFor(accessor);
        boolean isStrictCoerce = getDefaultIsCoerceStrictFor(accessor);
        return new ProxyKey(accessor, attributeKey, defaultValue, coercer, isStrictCoerce);
    }

    protected Object getAttributeKeyFor(Accessor accessor) {
        return attributeKeyFor(accessor);
    }

    protected Object getDefaultValueFor(Accessor accessor) {
        return defaultValueFor(accessor);
    }

    protected Coercer getCoercerFor(Accessor accessor) {
        return coercerFor(accessor);
    }

    protected boolean getDefaultIsCoerceStrictFor(Accessor accessor) {
        return isCoerceStrictFor(accessor);
    }
}
