// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation.joda;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Future;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;

public class FutureValidator implements ConstraintValidator<Future, Instant> {

  @Override
  public void initialize(final Future constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final Instant value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    final ZonedDateTime checkDate =
        JavaTimeHelpers.getZonedDateTimeWithStartAtBeginOfDay(
            LocalDate.now(ZoneId.of("UTC")), ZoneId.of("UTC"));

    return value.isAfter(checkDate.toInstant());
  }
}
