// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;

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
