// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.iec60870;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;

public class Iec60870AsduHandlerRegistryTest {
  private Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry;

  @BeforeEach
  public void setup() {
    this.iec60870AsduHandlerRegistry = new Iec60870AsduHandlerRegistry();
  }

  @Test
  public void getHandlerShouldReturnHandlerWhenPresent()
      throws Iec60870AsduHandlerNotFoundException {
    // Arrange
    final ASduType typeId = ASduType.M_ME_TF_1;
    final Iec60870AsduHandler expected = mock(Iec60870AsduHandler.class);
    this.iec60870AsduHandlerRegistry.registerHandler(typeId, expected);

    // Act
    final Iec60870AsduHandler actual = this.iec60870AsduHandlerRegistry.getHandler(typeId);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void getHandlerShouldThrowExceptionWhenNotPresent() {
    // Arrange
    final ASduType typeId = ASduType.M_ME_TF_1;
    final Class<?> expected = Iec60870AsduHandlerNotFoundException.class;

    // Act
    final Throwable actual =
        catchThrowable(() -> this.iec60870AsduHandlerRegistry.getHandler(typeId));

    // Assert
    assertThat(actual).isInstanceOf(expected);
  }
}
