/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.ClientConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.RequestMetadataFactory;

@ExtendWith(MockitoExtension.class)
public class ClientConnectionServiceImplTest {

    @InjectMocks
    private ClientConnectionServiceImpl clientConnectionService;

    @Spy
    private ClientConnectionCacheImpl connectionCache;

    @Mock
    private Client iec60870Client;

    @Mock
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Mock
    private ClientAsduHandlerRegistry clientAsduHandlerRegistry;

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#getConnection(org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata)}.
     *
     * @throws Exception
     */
    @Test
    void testGetConnectionShouldReturnExistingConnectionWhenInCache() throws Exception {
        // Arrange
        final String deviceIdentification = "DA_DVC_1";
        final Iec60870Device device = Iec60870DeviceFactory.createDistributionAutomationDevice(deviceIdentification);
        when(this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Optional.of(device));

        final RequestMetadata requestMetadata = RequestMetadataFactory.forDevice(deviceIdentification);
        final ClientConnection expectedConnection = ClientConnectionFactory.forDevice(deviceIdentification);
        this.connectionCache.addConnection(deviceIdentification, expectedConnection);

        // Act
        final ClientConnection actualConnection = this.clientConnectionService.getConnection(requestMetadata);

        // Assert
        assertThat(actualConnection).isEqualTo(expectedConnection);
    }

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#getConnection(org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata)}.
     *
     * @throws Exception
     */
    @Test
    void testGetConnectionShouldReturnExistingConnectionToGatewayDeviceWhenInCache() throws Exception {
        // Arrange
        final String deviceIdentification = "LM_DVC_1";
        final String gatewayDeviceIdentification = "LM_GATEWAY_1";
        final Iec60870Device device = Iec60870DeviceFactory.createLightMeasurementDevice(deviceIdentification,
                gatewayDeviceIdentification);
        when(this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Optional.of(device));

        final RequestMetadata requestMetadata = RequestMetadataFactory.forDevice(deviceIdentification);

        final ClientConnection expectedConnection = ClientConnectionFactory.forDevice(gatewayDeviceIdentification);
        this.connectionCache.addConnection(gatewayDeviceIdentification, expectedConnection);

        // Act
        final ClientConnection actualConnection = this.clientConnectionService.getConnection(requestMetadata);

        // Assert
        assertThat(actualConnection).isEqualTo(expectedConnection);
    }

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#getConnection(org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata)}.
     *
     * @throws Exception
     */
    @Test
    void testGetConnectionShouldReturnCreateConnectionWhenNotInCache() throws Exception {
        // Arrange
        final String deviceIdentification = "DA_DVC_1";
        final Iec60870Device device = Iec60870DeviceFactory.createDistributionAutomationDevice(deviceIdentification);
        when(this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Optional.of(device));

        final RequestMetadata requestMetadata = RequestMetadataFactory.forDevice(deviceIdentification);
        final ClientConnection expectedConnection = ClientConnectionFactory.forDevice(deviceIdentification);
        when(this.iec60870Client.connect(eq(expectedConnection.getConnectionParameters()),
                any(ConnectionEventListener.class))).thenReturn(expectedConnection);

        // Act
        final ClientConnection actualConnection = this.clientConnectionService.getConnection(requestMetadata);

        // Assert
        assertThat(actualConnection).isEqualTo(expectedConnection);
    }

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#getConnection(org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata)}.
     *
     * @throws Exception
     */
    @Test
    void testGetConnectionShouldReturnCreateConnectionToGatewayDeviceWhenNotInCache() throws Exception {
        // Arrange
        final String deviceIdentification = "LM_DVC_1";
        final String gatewayDeviceIdentification = "LM_GATEWAY_1";
        final Iec60870Device device = Iec60870DeviceFactory.createLightMeasurementDevice(deviceIdentification,
                gatewayDeviceIdentification);
        final Iec60870Device gateway = Iec60870DeviceFactory
                .createLightMeasurementGatewayDevice(gatewayDeviceIdentification);
        when(this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Optional.of(device));
        when(this.iec60870DeviceRepository.findByDeviceIdentification(gatewayDeviceIdentification))
                .thenReturn(Optional.of(gateway));

        final RequestMetadata requestMetadata = RequestMetadataFactory.forDevice(deviceIdentification);
        final ClientConnection expectedConnection = ClientConnectionFactory.forDevice(gatewayDeviceIdentification);
        when(this.iec60870Client.connect(eq(expectedConnection.getConnectionParameters()),
                any(ConnectionEventListener.class))).thenReturn(expectedConnection);

        // Act
        final ClientConnection actualConnection = this.clientConnectionService.getConnection(requestMetadata);

        // Assert
        assertThat(actualConnection).isEqualTo(expectedConnection);
    }

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#disconnect(java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    void testDisconnectString() throws Exception {
        // Arrange
        final String deviceIdentification = "DA_DVC_1";
        final ClientConnection clientConnection = ClientConnectionFactory.forDevice(deviceIdentification);
        this.connectionCache.addConnection(deviceIdentification, clientConnection);

        // Act
        this.clientConnectionService.disconnect(deviceIdentification);

        // Assert
        verify(this.iec60870Client).disconnect(clientConnection);
        verify(this.connectionCache).removeConnection(deviceIdentification);
    }

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#disconnect(org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection)}.
     *
     * @throws Exception
     */
    @Test
    void testDisconnectClientConnection() throws Exception {
        // Arrange
        final String deviceIdentification = "DA_DVC_1";
        final ClientConnection clientConnection = ClientConnectionFactory.forDevice(deviceIdentification);
        this.connectionCache.addConnection(deviceIdentification, clientConnection);

        // Act
        this.clientConnectionService.disconnect(clientConnection);

        // Assert
        verify(this.iec60870Client).disconnect(clientConnection);
        verify(this.connectionCache).removeConnection(deviceIdentification);
    }

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl#closeAllConnections()}.
     *
     * @throws Exception
     */
    @Test
    void testCloseAllConnections() throws Exception {
        // Arrange
        final String deviceIdentification1 = "DA_DVC_1";
        final ClientConnection clientConnection1 = ClientConnectionFactory.forDevice(deviceIdentification1);
        this.connectionCache.addConnection(deviceIdentification1, clientConnection1);
        final String deviceIdentification2 = "DA_DVC_2";
        final ClientConnection clientConnection2 = ClientConnectionFactory.forDevice(deviceIdentification2);
        this.connectionCache.addConnection(deviceIdentification2, clientConnection2);

        // Act
        this.clientConnectionService.closeAllConnections();

        // Assert
        verify(this.iec60870Client).disconnect(clientConnection1);
        verify(this.iec60870Client).disconnect(clientConnection2);
        verify(this.connectionCache).removeConnection(deviceIdentification1);
        verify(this.connectionCache).removeConnection(deviceIdentification2);
    }

}
