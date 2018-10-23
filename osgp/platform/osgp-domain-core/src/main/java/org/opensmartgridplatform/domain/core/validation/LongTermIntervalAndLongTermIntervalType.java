/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LongTermIntervalAndLongTermIntervalTypeValidator.class)
@Documented
public @interface LongTermIntervalAndLongTermIntervalType {

    String message() default "LongTermInterval and LongTermIntervalType must both be omitted or both be present. Further the permitted range for LongTermIntervalType.DAYS is from 1 to 30 and for LongTermIntervalType.MONTHS is from 1 to 12.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}