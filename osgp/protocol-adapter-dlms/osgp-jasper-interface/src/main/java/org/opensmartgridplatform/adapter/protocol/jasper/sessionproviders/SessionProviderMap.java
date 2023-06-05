// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
