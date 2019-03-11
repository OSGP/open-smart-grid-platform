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
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandASduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandASduHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

// SpringBootTest annotation needed here, to resolve autowired fields
@SpringBootTest
public class Iec60870ASduHandlerRegistryTests {
    // Mock the RTU Simulator to prevent the simulator from starting, which
    // might cause 'address already in use' exceptions while running tests
    @MockBean
    Iec60870RtuSimulator iec60870RtuSimulator;

    @Autowired
    Iec60870ASduHandlerRegistry iec60870aSduHandlerRegistry;

    @Autowired
    Iec60870InterrogationCommandASduHandler iec60870InterrogationCommandASduHandler;

    @Autowired
    Iec60870SingleCommandASduHandler iec60870SingleCommandASduHandler;

    @Test
    public void registryShouldThrowExceptionWhenHandlerIsNotFound() {
        // arrange
        // Type Id PRIVATE_255 is used here, as it is likely to not have a
        // handler implemented...
        final TypeId typeId = TypeId.PRIVATE_255;
        final Class<?> expected = Iec60870ASduHandlerNotFoundException.class;

        // act
        final Throwable actual = catchThrowable(() -> this.iec60870aSduHandlerRegistry.getHandler(typeId));

        // assert
        assertThat(actual).isInstanceOf(expected);
    }

    @Test
    public void registryShouldReturnInterrogationCommandHandlerForInterrogationCommandTypeId() {
        // arrange
        final TypeId typeId = TypeId.C_IC_NA_1;
        final Iec60870InterrogationCommandASduHandler expected = this.iec60870InterrogationCommandASduHandler;

        // act
        final Iec60870ASduHandler actual = this.iec60870aSduHandlerRegistry.getHandler(typeId);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void registryShouldReturnSingleCommandHandlerForSingleCommandTypeId() {
        // arrange
        final TypeId typeId = TypeId.C_SC_NA_1;
        final Iec60870SingleCommandASduHandler expected = this.iec60870SingleCommandASduHandler;

        // act
        final Iec60870ASduHandler actual = this.iec60870aSduHandlerRegistry.getHandler(typeId);

        // assert
        assertThat(actual).isEqualTo(expected);
    }
}
