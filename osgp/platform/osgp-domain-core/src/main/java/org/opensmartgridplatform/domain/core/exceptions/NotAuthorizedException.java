// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class NotAuthorizedException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2343397355361259276L;

  private static final String MESSAGE = "Organisation [%1$s] is not authorized for action";

  public NotAuthorizedException(final String organisationIdentification) {
    super(String.format(MESSAGE, organisationIdentification));
  }
}
