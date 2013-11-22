package org.ubercraft.sucre.attributes;

import static org.ubercraft.sucre.attributes.AttributesUtil.attributeKeyFor;

import org.ubercraft.sucre.reflect.Accessor;

public class DefaultLowerCaseProxyKeyFactory extends DefaultProxyKeyFactory {

    public static final ProxyKeyFactory DEFAULT_LOWER_CASE_PROXY_KEY_FACTORY = new DefaultLowerCaseProxyKeyFactory();

    @Override
    protected Object getAttributeKeyFor(Accessor accessor) {
        return attributeKeyFor(accessor).toLowerCase();
    }
}
