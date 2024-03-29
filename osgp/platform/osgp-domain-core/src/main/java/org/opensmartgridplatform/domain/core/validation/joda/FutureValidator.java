// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation.joda;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Future;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

public class FutureValidator implements ConstraintValidator<Future, ReadableInstant> {

  @Override
  public void initialize(final Future constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final ReadableInstant value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    final DateTime checkDate = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay();

    return value.isEqual(checkDate) || value.isAfter(checkDate);
  }
}
