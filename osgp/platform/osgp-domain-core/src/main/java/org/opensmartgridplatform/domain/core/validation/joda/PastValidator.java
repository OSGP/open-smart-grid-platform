// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation.joda;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Past;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

public class PastValidator implements ConstraintValidator<Past, ReadableInstant> {

  @Override
  public void initialize(final Past constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final ReadableInstant value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    final DateTime checkDate = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay();

    return value.isEqual(checkDate) || value.isBefore(checkDate);
  }
}
