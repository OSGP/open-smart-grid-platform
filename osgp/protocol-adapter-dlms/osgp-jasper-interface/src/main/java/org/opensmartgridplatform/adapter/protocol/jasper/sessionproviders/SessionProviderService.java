/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsSessionProviderService")
public class SessionProviderService {

  @Autowired private SessionProviderMap sessionProviderMap;

  public SessionProvider getSessionProvider(final String provider) {
    return this.sessionProviderMap.getProvider(provider);
  }
}
