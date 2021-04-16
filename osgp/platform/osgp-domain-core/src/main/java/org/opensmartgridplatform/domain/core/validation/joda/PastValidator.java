/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
