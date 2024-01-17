// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class ValidationException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 9063383618380310347L;

  private static final String DEFAULT_MESSAGE = "Validation Exception";

  private final transient Set<ConstraintViolation<?>> constraintViolations;

  public ValidationException() {
    super(DEFAULT_MESSAGE);
    this.constraintViolations = null;
  }

  public ValidationException(final String message) {
    super(message);
    this.constraintViolations = null;
  }

  public ValidationException(final Set<ConstraintViolation<?>> constraintViolations) {
    super(DEFAULT_MESSAGE + ", violations: " + convertToString(constraintViolations));
    this.constraintViolations = constraintViolations;
  }

  public ValidationException(
      final String message, final Set<ConstraintViolation<?>> constraintViolations) {
    super(message);
    this.constraintViolations = constraintViolations;
  }

  @SuppressWarnings(
      "squid:S1452") // Wildcard necessary here, generic is used for very different classes
  public Set<ConstraintViolation<?>> getConstraintViolations() {
    return this.constraintViolations;
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();

    result.append(DEFAULT_MESSAGE);

    if (this.constraintViolations != null && !this.constraintViolations.isEmpty()) {
      result.append(", violations: ");
      for (final ConstraintViolation<?> violation : this.constraintViolations) {
        result
            .append(violation.getPropertyPath())
            .append(", invalid value: ")
            .append(violation.getInvalidValue())
            .append("; ");
      }
    }

    return result.toString();
  }

  private static String convertToString(final Set<ConstraintViolation<?>> constraintViolations) {
    final StringBuilder violations = new StringBuilder();

    for (final ConstraintViolation<?> violation : constraintViolations) {

      if (!StringUtils.isBlank(violation.getMessage())) {
        violations.append(violation.getMessage());
      } else {
        violations.append(violation.getPropertyPath());
      }

      violations.append("; ");
    }

    return violations.toString();
  }
}
