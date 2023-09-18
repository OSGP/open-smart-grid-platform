// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.Optional;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public abstract class SessionProvider {

  protected SessionProviderMap sessionProviderMap;

  public SessionProvider(final SessionProviderMap sessionProviderMap) {
    this.sessionProviderMap = sessionProviderMap;
  }

  public abstract Optional<String> getIpAddress(String deviceIdentification, String iccId)
      throws OsgpException;
}
