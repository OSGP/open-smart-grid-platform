/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.iec60870;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmuc.j60870.Connection;

public class Iec60870ConnectionRegistryTest {

    private Iec60870ConnectionRegistry iec60870ConnectionRegistry;

    @Before
    public void setup() {
        this.iec60870ConnectionRegistry = new Iec60870ConnectionRegistry();
    }

    @Test
    public void getAllConnectionsShouldReturnAllConnectionsPresentInRegistry() {
        // Arrange
        final Connection connection = mock(Connection.class);
        this.iec60870ConnectionRegistry.registerConnection(1, connection);
        this.iec60870ConnectionRegistry.registerConnection(2, connection);
        final List<Connection> expected = java.util.Arrays.asList(connection, connection);

        // Act
        final List<Connection> actual = this.iec60870ConnectionRegistry.getAllConnections();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getConnectionShouldReturnConnectionWhenConnectionIsPresent() {
        // Arrange
        final int connectionId = 1;
        final Connection expected = mock(Connection.class);
        this.iec60870ConnectionRegistry.registerConnection(connectionId, expected);

        // Act
        final Connection actual = this.iec60870ConnectionRegistry.getConnection(connectionId);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getConnectionShouldReturnNullWhenConnectionIsNotPresent() {
        // Arrange
        final int unknownConnectionId = 1;

        // Act
        final Connection actual = this.iec60870ConnectionRegistry.getConnection(unknownConnectionId);

        // Assert
        assertThat(actual).isNull();
    }

    @Test
    public void registerConnectionShouldAddConnectionToRegistry() {
        // Arrange
        final int connectionId = 1;
        final Connection connection = mock(Connection.class);
        final int expectedNumberOfConnections = 1;

        // Act
        this.iec60870ConnectionRegistry.registerConnection(connectionId, connection);
        final int actualNumberOfConnection = this.iec60870ConnectionRegistry.getAllConnections().size();

        // Assert
        assertThat(actualNumberOfConnection).isEqualTo(expectedNumberOfConnections);
    }

    @Test
    public void unregisterConnectionShouldRemoveConnectionFromRegistry() {
        // Arrange
        final int connectionId = 1;
        final Connection connection = mock(Connection.class);
        this.iec60870ConnectionRegistry.registerConnection(connectionId, connection);
        final int expectedNumberOfConnections = 0;

        // Act
        this.iec60870ConnectionRegistry.unregisterConnection(connectionId);
        final int actualNumberOfConnections = this.iec60870ConnectionRegistry.getAllConnections().size();

        // Assert
        assertThat(actualNumberOfConnections).isEqualTo(expectedNumberOfConnections);
    }
}
