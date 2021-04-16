/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration;

public class NumberOfLightsAndIndexAddressMapValidator
    implements ConstraintValidator<NumberOfLightsAndIndexAddressMap, DaliConfiguration> {
  @Override
  public void initialize(final NumberOfLightsAndIndexAddressMap constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final DaliConfiguration value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    // If no number of lights or index-address map is given we cannot
    // perform this validation.
    if (value.getNumberOfLights() == null || value.getIndexAddressMap() == null) {
      return true;
    }

    return value.getNumberOfLights() == value.getIndexAddressMap().size();
  }
}
