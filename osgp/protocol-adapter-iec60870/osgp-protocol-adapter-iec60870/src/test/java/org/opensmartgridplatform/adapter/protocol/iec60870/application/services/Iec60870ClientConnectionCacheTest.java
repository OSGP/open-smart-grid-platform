/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCacheImpl;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;

public class Iec60870ClientConnectionCacheTest {

    private final static String KEY = "TestKey";

    private ClientConnectionCache cache;

    @BeforeEach
    public void setup() {
        this.cache = new ClientConnectionCacheImpl();
    }

    @Test
    public void shouldBeAbleToAddConnectionToCache() throws ClientConnectionAlreadyInCacheException {
        // Arrange
        final ClientConnection connection = new DeviceConnection(null, null);

        // Act
        this.cache.addConnection(KEY, connection);

        // Assert
        assertThat(this.cache.getConnection(KEY)).isEqualTo(connection);
    }

    @Test
    public void shouldBeAbleToRemoveConnectionFromCache() throws ClientConnectionAlreadyInCacheException {
        // Arrange
        final ClientConnection connection = new DeviceConnection(null, null);
        this.cache.addConnection(KEY, connection);

        // Act
        this.cache.removeConnection(KEY);

        // Assert
        assertThat(this.cache.getConnection(KEY)).isNull();
    }
}
