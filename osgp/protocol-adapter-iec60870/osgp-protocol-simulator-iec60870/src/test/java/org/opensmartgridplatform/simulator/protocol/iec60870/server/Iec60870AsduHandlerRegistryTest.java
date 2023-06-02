//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandler;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandAsduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandAsduHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Iec60870AsduHandlerRegistryTest {
  // Mock the RTU Simulator to prevent the simulator from starting, which
  // might cause 'address already in use' exceptions while running tests
  @MockBean private Iec60870Server iec60870Server;

  @Autowired private Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry;

  @Autowired
  private Iec60870InterrogationCommandAsduHandler iec60870InterrogationCommandAsduHandler;

  @Autowired private Iec60870SingleCommandAsduHandler iec60870SingleCommandAsduHandler;

  @Test
  public void registryShouldThrowExceptionWhenHandlerIsNotFound() {
    // arrange
    // Type Id PRIVATE_255 is used here, as it is likely to not have a
    // handler implemented...
    final ASduType asduType = ASduType.PRIVATE_255;
    final Class<?> expected = Iec60870AsduHandlerNotFoundException.class;

    // act
    final Throwable actual =
        catchThrowable(() -> this.iec60870AsduHandlerRegistry.getHandler(asduType));

    // assert
    assertThat(actual).isInstanceOf(expected);
  }

  @Test
  public void registryShouldReturnInterrogationCommandHandlerForInterrogationCommandAsduType()
      throws Exception {
    // arrange
    final ASduType asduType = ASduType.C_IC_NA_1;
    final Iec60870InterrogationCommandAsduHandler expected =
        this.iec60870InterrogationCommandAsduHandler;

    // act
    final Iec60870AsduHandler actual = this.iec60870AsduHandlerRegistry.getHandler(asduType);

    // assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void registryShouldReturnSingleCommandHandlerForSingleCommandAsduType() throws Exception {
    // arrange
    final ASduType asduType = ASduType.C_SC_NA_1;
    final Iec60870SingleCommandAsduHandler expected = this.iec60870SingleCommandAsduHandler;

    // act
    final Iec60870AsduHandler actual = this.iec60870AsduHandlerRegistry.getHandler(asduType);

    // assert
    assertThat(actual).isEqualTo(expected);
  }
}
