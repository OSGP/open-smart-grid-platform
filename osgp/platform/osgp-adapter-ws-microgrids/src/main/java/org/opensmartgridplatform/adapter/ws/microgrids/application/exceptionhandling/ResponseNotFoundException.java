// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.application.exceptionhandling;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ResponseNotFoundException extends OsgpException {

  private static final long serialVersionUID = 1706342594144271262L;

  public ResponseNotFoundException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }
}
