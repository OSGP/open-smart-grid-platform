// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptyOrganisationIdentificationSoapHeaderException extends EmptySoapHeaderException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3236913216772885583L;

  public EmptyOrganisationIdentificationSoapHeaderException(
      final String organisationIdentification) {
    super("OrganisationIdentification", organisationIdentification);
  }
}
