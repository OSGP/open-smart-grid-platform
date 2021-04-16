/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class SetRandomisationSettingsRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -381163520662276869L;

  private final int directAttach;
  private final int randomisationStartWindow;
  private final int multiplicationFactor;
  private final int numberOfRetries;

  static final int MIN_VALUE_DIRECT_ATTACH = 0;
  static final int MAX_VALUE_DIRECT_ATTACH = 1;
  static final int MIN_VALUE_RANDOMIZATION_START_WINDOW = 1;
  static final int MAX_VALUE_RANDOMIZATION_START_WINDOW = 65535;
  static final int MIN_VALUE_MULTIPLICATION_FACTOR = 1;
  static final int MAX_VALUE_MULTIPLICATION_FACTOR = 7;
  static final int MIN_VALUE_NUMBER_OF_RETRIES = 1;
  static final int MAX_VALUE_NUMBER_OF_RETRIES = 31;

  SetRandomisationSettingsRequestData(
      final int directAttach,
      final int randomisationStartWindow,
      final int multiplicationFactor,
      final int numberOfRetries) {
    this.directAttach = directAttach;
    this.randomisationStartWindow = randomisationStartWindow;
    this.multiplicationFactor = multiplicationFactor;
    this.numberOfRetries = numberOfRetries;
  }

  @Override
  public void validate() throws FunctionalException {

    if (this.directAttach < MIN_VALUE_DIRECT_ATTACH
        || this.directAttach > MAX_VALUE_DIRECT_ATTACH) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new Exception(
              String.format(
                  "DirectAttach value range failed. (%s-%s)",
                  MIN_VALUE_DIRECT_ATTACH, MAX_VALUE_DIRECT_ATTACH)));
    }

    if (this.randomisationStartWindow < MIN_VALUE_RANDOMIZATION_START_WINDOW
        || this.randomisationStartWindow > MAX_VALUE_RANDOMIZATION_START_WINDOW) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new Exception(
              String.format(
                  "RandomisationStartWindow value range failed. (%s-%s)",
                  MIN_VALUE_RANDOMIZATION_START_WINDOW, MAX_VALUE_RANDOMIZATION_START_WINDOW)));
    }

    if (this.multiplicationFactor < MIN_VALUE_MULTIPLICATION_FACTOR
        || this.multiplicationFactor > MAX_VALUE_MULTIPLICATION_FACTOR) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new Exception(
              String.format(
                  "MultiplicationFactor value range failed. (%s-%s)",
                  MIN_VALUE_MULTIPLICATION_FACTOR, MAX_VALUE_MULTIPLICATION_FACTOR)));
    }

    if (this.numberOfRetries < MIN_VALUE_NUMBER_OF_RETRIES
        || this.numberOfRetries > MAX_VALUE_NUMBER_OF_RETRIES) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new Exception(
              String.format(
                  "NumberOfRetries value range failed. (%s-%s)",
                  MIN_VALUE_NUMBER_OF_RETRIES, MAX_VALUE_NUMBER_OF_RETRIES)));
    }
  }

  public int getDirectAttach() {
    return this.directAttach;
  }

  public int getRandomisationStartWindow() {
    return this.randomisationStartWindow;
  }

  public int getMultiplicationFactor() {
    return this.multiplicationFactor;
  }

  public int getNumberOfRetries() {
    return this.numberOfRetries;
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_RANDOMISATION_SETTINGS;
  }
}
