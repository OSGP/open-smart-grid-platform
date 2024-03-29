// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.Set;

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
