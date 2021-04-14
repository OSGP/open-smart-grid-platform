/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SessionProviderMap {
  private final Map<SessionProviderEnum, SessionProvider> map = new HashMap<>();

  public SessionProvider getProvider(final String provider) {
    final SessionProviderEnum sessionProviderEnum = SessionProviderEnum.valueOf(provider);
    return this.map.get(sessionProviderEnum);
  }

  public void addProvider(
      final SessionProviderEnum provider, final SessionProvider sessionProvider) {
    this.map.put(provider, sessionProvider);
  }
}
