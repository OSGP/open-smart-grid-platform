/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandler;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandASduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandASduHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Iec60870ASduHandlerRegistryTest {
    // Mock the RTU Simulator to prevent the simulator from starting, which
    // might cause 'address already in use' exceptions while running tests
    @MockBean
    private Iec60870Server iec60870Server;

    @Autowired
    private Iec60870ASduHandlerRegistry iec60870aSduHandlerRegistry;

    @Autowired
    private Iec60870InterrogationCommandASduHandler iec60870InterrogationCommandASduHandler;

    @Autowired
    private Iec60870SingleCommandASduHandler iec60870SingleCommandASduHandler;

    @Test
    public void registryShouldThrowExceptionWhenHandlerIsNotFound() {
        // arrange
        // Type Id PRIVATE_255 is used here, as it is likely to not have a
        // handler implemented...
        final ASduType asduType = ASduType.PRIVATE_255;
        final Class<?> expected = Iec60870ASduHandlerNotFoundException.class;

        // act
        final Throwable actual = catchThrowable(() -> this.iec60870aSduHandlerRegistry.getHandler(asduType));

        // assert
        assertThat(actual).isInstanceOf(expected);
    }

    @Test
    public void registryShouldReturnInterrogationCommandHandlerForInterrogationCommandTypeId() throws Exception {
        // arrange
        final ASduType asduType = ASduType.C_IC_NA_1;
        final Iec60870InterrogationCommandASduHandler expected = this.iec60870InterrogationCommandASduHandler;

        // act
        final Iec60870ASduHandler actual = this.iec60870aSduHandlerRegistry.getHandler(asduType);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void registryShouldReturnSingleCommandHandlerForSingleCommandTypeId() throws Exception {
        // arrange
        final ASduType asduType = ASduType.C_SC_NA_1;
        final Iec60870SingleCommandASduHandler expected = this.iec60870SingleCommandASduHandler;

        // act
        final Iec60870ASduHandler actual = this.iec60870aSduHandlerRegistry.getHandler(asduType);

        // assert
        assertThat(actual).isEqualTo(expected);
    }
}
