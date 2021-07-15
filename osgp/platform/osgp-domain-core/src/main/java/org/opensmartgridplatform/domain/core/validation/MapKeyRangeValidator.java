/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MapKeyRangeValidator implements ConstraintValidator<MapKeyRange, Map<Integer, ?>> {

  private int min;
  private int max;

  @Override
  public void initialize(final MapKeyRange constraintAnnotation) {
    this.min = constraintAnnotation.min();
    this.max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(final Map<Integer, ?> map, final ConstraintValidatorContext context) {
    if (map == null) {
      return true;
    }

    final Set<Integer> keys = map.keySet();

    for (final Integer key : keys) {
      if (key < this.min || key > this.max) {
        return false;
      }
    }

    return true;
  }
}
