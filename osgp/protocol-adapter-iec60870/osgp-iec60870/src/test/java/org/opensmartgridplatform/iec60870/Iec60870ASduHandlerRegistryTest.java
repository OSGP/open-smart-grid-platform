/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.iec60870;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.TypeId;

public class Iec60870ASduHandlerRegistryTest {
    private Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;

    @BeforeEach
    public void setup() {
        this.iec60870ASduHandlerRegistry = new Iec60870ASduHandlerRegistry();
    }

    @Test
    public void getHandlerShouldReturnHandlerWhenPresent() throws Iec60870ASduHandlerNotFoundException {
        // Arrange
        final TypeId typeId = TypeId.M_ME_TF_1;
        final Iec60870ASduHandler expected = mock(Iec60870ASduHandler.class);
        this.iec60870ASduHandlerRegistry.registerHandler(typeId, expected);

        // Act
        final Iec60870ASduHandler actual = this.iec60870ASduHandlerRegistry.getHandler(typeId);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getHandlerShouldThrowExceptionWhenNotPresent() {
        // Arrange
        final TypeId typeId = TypeId.M_ME_TF_1;
        final Class<?> expected = Iec60870ASduHandlerNotFoundException.class;

        // Act
        final Throwable actual = catchThrowable(() -> this.iec60870ASduHandlerRegistry.getHandler(typeId));

        // Assert
        assertThat(actual).isInstanceOf(expected);
    }
}
