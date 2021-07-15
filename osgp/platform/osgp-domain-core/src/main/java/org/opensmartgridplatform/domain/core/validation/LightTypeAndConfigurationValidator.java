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
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;

public class LightTypeAndConfigurationValidator
    implements ConstraintValidator<LightTypeAndConfiguration, Configuration> {

  @Override
  public void initialize(final LightTypeAndConfiguration constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final Configuration value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    // If no light type is given only one of the configuration is allowed.
    if (value.getLightType() == null) {
      return value.getDaliConfiguration() == null || value.getRelayConfiguration() == null;
    }
    switch (value.getLightType()) {
      case DALI:
        return value.getRelayConfiguration() == null;
      case ONE_TO_TEN_VOLT:
      case ONE_TO_TEN_VOLT_REVERSE:
        return value.getDaliConfiguration() == null && value.getRelayConfiguration() == null;
      case RELAY:
        return value.getDaliConfiguration() == null;
      default:
        return false;
    }
  }
}
