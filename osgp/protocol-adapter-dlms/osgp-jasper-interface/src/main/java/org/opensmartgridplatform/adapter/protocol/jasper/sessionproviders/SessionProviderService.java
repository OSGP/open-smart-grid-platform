//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
