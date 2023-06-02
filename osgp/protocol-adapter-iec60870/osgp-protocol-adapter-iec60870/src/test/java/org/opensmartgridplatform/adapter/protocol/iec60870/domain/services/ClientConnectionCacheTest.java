//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;

class ClientConnectionCacheTest {

  private static final String KEY = "TestKey";

  private ClientConnectionCache cache;

  @BeforeEach
  public void setup() {
    this.cache = new ClientConnectionCache();
  }

  @Test
  void shouldBeAbleToAddConnectionToCache() throws ClientConnectionAlreadyInCacheException {
    // Arrange
    final ClientConnection connection = new DeviceConnection(null, null);

    // Act
    this.cache.addConnection(KEY, connection);

    // Assert
    assertThat(this.cache.getConnection(KEY)).isEqualTo(connection);
  }

  @Test
  void shouldBeAbleToRemoveConnectionFromCache() throws ClientConnectionAlreadyInCacheException {
    // Arrange
    final ClientConnection connection = new DeviceConnection(null, null);
    this.cache.addConnection(KEY, connection);

    // Act
    this.cache.removeConnection(KEY);

    // Assert
    assertThat(this.cache.getConnection(KEY)).isNull();
  }
}
