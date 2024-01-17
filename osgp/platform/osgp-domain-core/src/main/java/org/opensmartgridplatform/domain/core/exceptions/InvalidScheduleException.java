// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import java.util.Set;
import jakarta.validation.ConstraintViolation;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidScheduleException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3700196675363961125L;

  private static final String MESSAGE = "Invalid schedule, validation errors: %1$s";

  public InvalidScheduleException(
      final Set<ConstraintViolation<ScheduleEntry>> constraintViolations) {
    super(String.format(MESSAGE, convertToString(constraintViolations)));
  }

  private static String convertToString(
      final Set<ConstraintViolation<ScheduleEntry>> constraintViolations) {
    final StringBuilder violations = new StringBuilder();

    for (final ConstraintViolation<ScheduleEntry> violation : constraintViolations) {
      violations.append(violation.getPropertyPath() + " " + violation.getMessage() + "; ");
    }

    return violations.toString();
  }
}
