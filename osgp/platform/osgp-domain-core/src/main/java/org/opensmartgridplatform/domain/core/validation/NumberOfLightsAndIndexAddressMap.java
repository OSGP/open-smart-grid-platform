// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberOfLightsAndIndexAddressMapValidator.class)
@Documented
public @interface NumberOfLightsAndIndexAddressMap {
  String message() default
      "Number of lights in dali configuration must be equal to number of index-address maps.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
