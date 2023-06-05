// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
