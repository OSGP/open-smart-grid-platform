// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MapKeyRangeValidator.class)
@Documented
public @interface MapKeyRange {

  String message() default "Map key must be within range";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int min() default 0;

  int max() default Integer.MAX_VALUE;
}
