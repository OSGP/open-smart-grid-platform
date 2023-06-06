// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
