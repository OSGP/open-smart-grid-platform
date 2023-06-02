//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.endpoint;

import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public interface WebserviceEndpoint {

  void handleException(final Exception e) throws OsgpException;

  void saveResponseUrlIfNeeded(final String correlationUid, final String responseUrl);
}
