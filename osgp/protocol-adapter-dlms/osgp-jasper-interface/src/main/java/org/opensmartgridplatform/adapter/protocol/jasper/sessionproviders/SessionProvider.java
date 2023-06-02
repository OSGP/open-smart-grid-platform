//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SessionProvider {

  @Autowired protected SessionProviderMap sessionProviderMap;

  public abstract String getIpAddress(String iccId) throws OsgpException;
}
