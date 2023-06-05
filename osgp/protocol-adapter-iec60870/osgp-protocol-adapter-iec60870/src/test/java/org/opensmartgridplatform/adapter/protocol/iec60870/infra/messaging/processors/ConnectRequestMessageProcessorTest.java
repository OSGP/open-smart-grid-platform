// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.GeneralInterrogationService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.RequestMetadataFactory;

@ExtendWith(MockitoExtension.class)
public class ConnectRequestMessageProcessorTest {

  private static final String DEVICE_IDENTIFICATION = "LIGHT_MEASUREMENT_GATEWAY_001";

  @InjectMocks private ConnectRequestMessageProcessor connectRequestMessageProcessor;

  @Mock private Connection connection;

  @Mock private GeneralInterrogationService generalInterrogationService;

  @Test
  void testProcessShouldSendGeneralInInterrogation() throws Exception {
    // Arrange
    final ConnectionParameters connectionParameters =
        ConnectionParameters.newBuilder().deviceIdentification(DEVICE_IDENTIFICATION).build();
    final DeviceConnection deviceConnection =
        new DeviceConnection(this.connection, connectionParameters);
    final RequestMetadata requestMetadata = RequestMetadataFactory.forDevice(DEVICE_IDENTIFICATION);

    // Act
    this.connectRequestMessageProcessor.process(deviceConnection, requestMetadata);

    // Assert
    verify(this.generalInterrogationService)
        .sendGeneralInterrogation(deviceConnection, requestMetadata);
  }
}
