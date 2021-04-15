/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.Connection;

public class Iec60870ConnectionRegistryTest {

  private Iec60870ConnectionRegistry iec60870ConnectionRegistry;

  @BeforeEach
  public void setup() {
    this.iec60870ConnectionRegistry = new Iec60870ConnectionRegistry();
  }

  @Test
  public void getAllConnectionsShouldReturnAllConnectionsPresentInRegistry() {
    // Arrange
    final Connection connection1 = mock(Connection.class);
    final Connection connection2 = mock(Connection.class);
    this.iec60870ConnectionRegistry.registerConnection(connection1);
    this.iec60870ConnectionRegistry.registerConnection(connection2);
    final Set<Connection> expected = new HashSet<>(Arrays.asList(connection1, connection2));

    // Act
    final Set<Connection> actual = this.iec60870ConnectionRegistry.getAllConnections();

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void registerConnectionShouldAddConnectionToRegistry() {
    // Arrange
    final Connection connection = mock(Connection.class);
    final int expectedNumberOfConnections = 1;

    // Act
    this.iec60870ConnectionRegistry.registerConnection(connection);
    final int actualNumberOfConnection = this.iec60870ConnectionRegistry.getAllConnections().size();

    // Assert
    assertThat(actualNumberOfConnection).isEqualTo(expectedNumberOfConnections);
  }

  @Test
  public void unregisterConnectionShouldRemoveConnectionFromRegistry() {
    // Arrange
    final Connection connection = mock(Connection.class);
    this.iec60870ConnectionRegistry.registerConnection(connection);
    final int expectedNumberOfConnections = 0;

    // Act
    this.iec60870ConnectionRegistry.unregisterConnection(connection);
    final int actualNumberOfConnections =
        this.iec60870ConnectionRegistry.getAllConnections().size();

    // Assert
    assertThat(actualNumberOfConnections).isEqualTo(expectedNumberOfConnections);
  }
}
