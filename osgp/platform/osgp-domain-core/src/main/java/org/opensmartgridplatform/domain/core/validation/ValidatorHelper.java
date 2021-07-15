/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidatorContext;

/**
 * Helper class for validators which require multiple validation messages.
 *
 * @author hrooden
 */
public class ValidatorHelper {

  private final List<String> messages = new ArrayList<String>();

  /**
   * Add a violation message for reporting.
   *
   * @param message
   */
  protected void addMessage(final String message) {
    this.messages.add(message);
  }

  /**
   * Add the stored messages to the context.
   *
   * @param context
   * @return true when no messages are stored (ie failed validations) or false when validation
   *     failures are stored in context.
   */
  protected boolean isValid(final ConstraintValidatorContext context) {
    if (this.messages.isEmpty()) {
      return true;
    }

    context.disableDefaultConstraintViolation();

    for (final String message : this.messages) {
      context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    return false;
  }
}
