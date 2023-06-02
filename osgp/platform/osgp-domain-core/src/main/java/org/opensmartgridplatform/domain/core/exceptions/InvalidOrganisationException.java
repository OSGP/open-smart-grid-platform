//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.exceptions;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidOrganisationException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2734765757362133547L;

  private static final String MESSAGE = "Invalid organisation [%1$s], validation errors: %2$s";

  public InvalidOrganisationException(
      final Organisation organisation,
      final Set<ConstraintViolation<Organisation>> constraintViolations) {
    super(String.format(MESSAGE, organisation, convertToString(constraintViolations)));
  }

  private static String convertToString(
      final Set<ConstraintViolation<Organisation>> constraintViolations) {
    final StringBuilder violations = new StringBuilder();

    for (final ConstraintViolation<Organisation> violation : constraintViolations) {
      violations.append(violation.getMessage() + "\n");
    }

    return violations.toString();
  }
}
