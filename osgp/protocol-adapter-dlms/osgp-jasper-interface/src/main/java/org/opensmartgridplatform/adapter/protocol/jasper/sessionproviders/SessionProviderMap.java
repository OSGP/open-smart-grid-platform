// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionProviderMap {
  private final Map<SessionProviderEnum, SessionProvider> map = new HashMap<>();

  public SessionProvider getProvider(final String provider) {
    if (StringUtils.isEmpty(provider)) {
      log.error("Could not find SessionProvider because of null value in provider parameter");
      return null;
    }
    try {
      final var sessionProviderEnum = SessionProviderEnum.valueOf(provider);
      return this.map.get(sessionProviderEnum);
    } catch (final IllegalArgumentException e) {
      log.error("Could not find SessionProvider for unknown provider: {}", provider);
      return null;
    }
  }

  public void addProvider(
      final SessionProviderEnum provider, final SessionProvider sessionProvider) {
    this.map.put(provider, sessionProvider);
  }
}
