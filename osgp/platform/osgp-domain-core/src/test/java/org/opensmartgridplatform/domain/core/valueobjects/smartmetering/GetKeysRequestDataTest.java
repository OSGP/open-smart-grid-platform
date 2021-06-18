/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

class GetKeysRequestDataTest {

  @Test
  void testEmptySecretTypesList() throws FunctionalException {
    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              new GetKeysRequestData(Collections.emptyList()).validate();
            });
  }
}
