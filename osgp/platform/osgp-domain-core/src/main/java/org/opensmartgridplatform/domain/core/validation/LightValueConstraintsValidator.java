// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;

public class LightValueConstraintsValidator
    implements ConstraintValidator<LightValueConstraints, LightValue> {

  private static final String CHECK_DIM_VALUE_EMPTY_WHEN_LIGHT_OFF =
      "Dim value may not be set when light is switched off";

  // Index should be between 0 and 6 --> using annotations
  // Dim Value should be between 1 and 100 --> using annotations
  // If On = false, Dim Value may not be set

  @Override
  public void initialize(final LightValueConstraints constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final LightValue lightValue, final ConstraintValidatorContext context) {
    final ValidatorHelper helper = new ValidatorHelper();

    this.checkDimValue(helper, lightValue);

    return helper.isValid(context);
  }

  private void checkDimValue(final ValidatorHelper helper, final LightValue lightValue) {
    if (!lightValue.isOn() && lightValue.getDimValue() != null) {
      helper.addMessage(CHECK_DIM_VALUE_EMPTY_WHEN_LIGHT_OFF);
    }
  }
}
