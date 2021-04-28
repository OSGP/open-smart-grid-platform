/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetRandomisationSettingsRequestDataTest {

  @Test
  public void testInvalidDirectAttach() throws FunctionalException {
    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              new SetRandomisationSettingsRequestData(
                      SetRandomisationSettingsRequestData.MIN_VALUE_DIRECT_ATTACH - 1,
                      SetRandomisationSettingsRequestData.MIN_VALUE_RANDOMIZATION_START_WINDOW,
                      SetRandomisationSettingsRequestData.MIN_VALUE_MULTIPLICATION_FACTOR,
                      SetRandomisationSettingsRequestData.MIN_VALUE_NUMBER_OF_RETRIES)
                  .validate();
            });
  }

  @Test
  public void testInvalidMultiplicationFactor() throws FunctionalException {
    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              new SetRandomisationSettingsRequestData(
                      SetRandomisationSettingsRequestData.MIN_VALUE_DIRECT_ATTACH,
                      SetRandomisationSettingsRequestData.MAX_VALUE_RANDOMIZATION_START_WINDOW,
                      SetRandomisationSettingsRequestData.MAX_VALUE_MULTIPLICATION_FACTOR + 1,
                      SetRandomisationSettingsRequestData.MIN_VALUE_NUMBER_OF_RETRIES)
                  .validate();
            });
  }

  @Test
  public void testInvalidNumberOfRetries() throws FunctionalException {
    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              new SetRandomisationSettingsRequestData(
                      SetRandomisationSettingsRequestData.MIN_VALUE_DIRECT_ATTACH,
                      SetRandomisationSettingsRequestData.MAX_VALUE_RANDOMIZATION_START_WINDOW,
                      SetRandomisationSettingsRequestData.MAX_VALUE_MULTIPLICATION_FACTOR,
                      SetRandomisationSettingsRequestData.MAX_VALUE_NUMBER_OF_RETRIES + 1)
                  .validate();
            });
  }

  @Test
  public void testInvalidRandomisationStartWindow() throws FunctionalException {
    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              new SetRandomisationSettingsRequestData(
                      SetRandomisationSettingsRequestData.MIN_VALUE_DIRECT_ATTACH,
                      SetRandomisationSettingsRequestData.MAX_VALUE_RANDOMIZATION_START_WINDOW + 1,
                      SetRandomisationSettingsRequestData.MIN_VALUE_MULTIPLICATION_FACTOR,
                      SetRandomisationSettingsRequestData.MIN_VALUE_NUMBER_OF_RETRIES)
                  .validate();
            });
  }

  @Test
  public void testValidRequestData() {
    try {
      new SetRandomisationSettingsRequestData(
              SetRandomisationSettingsRequestData.MAX_VALUE_DIRECT_ATTACH,
              SetRandomisationSettingsRequestData.MIN_VALUE_RANDOMIZATION_START_WINDOW,
              SetRandomisationSettingsRequestData.MIN_VALUE_MULTIPLICATION_FACTOR,
              SetRandomisationSettingsRequestData.MIN_VALUE_NUMBER_OF_RETRIES)
          .validate();
    } catch (final FunctionalException e) {
      fail("");
    }
  }
}
