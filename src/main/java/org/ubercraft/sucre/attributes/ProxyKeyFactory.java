package org.ubercraft.sucre.attributes;

import org.ubercraft.sucre.reflect.Accessor;

public interface ProxyKeyFactory {

    ProxyKey proxyKeyFor(Accessor accessor);
}
