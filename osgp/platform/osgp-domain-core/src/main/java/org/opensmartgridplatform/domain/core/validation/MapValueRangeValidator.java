/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import java.util.Collection;
import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MapValueRangeValidator implements ConstraintValidator<MapValueRange, Map<?, Integer>> {

  private int min;
  private int max;

  @Override
  public void initialize(final MapValueRange constraintAnnotation) {
    this.min = constraintAnnotation.min();
    this.max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(final Map<?, Integer> map, final ConstraintValidatorContext context) {
    if (map == null) {
      return true;
    }

    final Collection<Integer> values = map.values();

    for (final Integer value : values) {
      if (value < this.min || value > this.max) {
        return false;
      }
    }

    return true;
  }
}
