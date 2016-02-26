package org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SessionProviderMap {
    private Map<SessionProviderEnum, SessionProvider> map = new HashMap<>();

    public SessionProvider getProvider(final String provider) {
        final SessionProviderEnum sessionProviderEnum = SessionProviderEnum.valueOf(provider);
        return this.map.get(sessionProviderEnum);
    }

    public void addProvider(final SessionProviderEnum provider, final SessionProvider sessionProvider) {
        this.map.put(provider, sessionProvider);
    }
}
