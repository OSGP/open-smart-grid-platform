//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.RequestMetadataFactory;

@ExtendWith(MockitoExtension.class)
class GeneralInterrogationServiceTest {
  private static final String DEVICE_IDENTIFICATION = "DEVICE-001";

  @InjectMocks private GeneralInterrogationService generalInterrogationService;

  @Mock private Connection connection;

  @Mock private LoggingService loggingService;

  /**
   * Test method for {@link
   * org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.ConnectRequestMessageProcessor#process(org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection,
   * org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata)}.
   *
   * @throws Exception
   */
  @Test
  void testSendGeneralInterrogationShouldLogSameAsduAsUsedInInterrogation() throws Exception {
    // Arrange
    final ConnectionParameters connectionParameters =
        ConnectionParameters.newBuilder().deviceIdentification(DEVICE_IDENTIFICATION).build();
    final DeviceConnection deviceConnection =
        new DeviceConnection(this.connection, connectionParameters);
    final RequestMetadata requestMetadata = RequestMetadataFactory.forDevice(DEVICE_IDENTIFICATION);
    doCallRealMethod()
        .when(this.connection)
        .interrogation(
            anyInt(), any(CauseOfTransmission.class), any(IeQualifierOfInterrogation.class));

    // Act
    this.generalInterrogationService.sendGeneralInterrogation(deviceConnection, requestMetadata);

    // Assert
    final ArgumentCaptor<ASdu> asduCaptor = ArgumentCaptor.forClass(ASdu.class);
    final ArgumentCaptor<LogItem> logItemCaptor = ArgumentCaptor.forClass(LogItem.class);
    verify(this.connection).send(asduCaptor.capture());
    verify(this.loggingService).log(logItemCaptor.capture());
    assertThat(logItemCaptor.getValue().getMessage()).isEqualTo(asduCaptor.getValue().toString());
  }
}
