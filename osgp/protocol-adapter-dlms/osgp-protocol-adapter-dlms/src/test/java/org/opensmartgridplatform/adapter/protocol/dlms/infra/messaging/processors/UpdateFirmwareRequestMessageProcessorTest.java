// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.throttling.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RetryHeaderFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateFirmwareRequestMessageProcessorTest {
  @Mock protected DlmsConnectionHelper connectionHelper;

  @Mock protected DeviceResponseMessageSender responseMessageSender;

  @Mock private RetryHeaderFactory retryHeaderFactory;

  @Mock private ConfigurationService configurationService;

  @Mock private FirmwareService firmwareService;

  @Mock private OsgpRequestMessageSender osgpRequestMessageSender;

  @Mock private DomainHelperService domainHelperService;

  @Mock private DlmsConnectionManager dlmsConnectionManagerMock;

  @Mock private DlmsMessageListener messageListenerMock;

  @Mock private ThrottlingService throttlingService;

  @Mock private ThrottlingClientConfig throttlingClientConfig;

  private DlmsDevice device;

  @InjectMocks private UpdateFirmwareRequestMessageProcessor processor;

  @BeforeEach
  void setup() throws OsgpException {

    this.device = new DlmsDeviceBuilder().withHls5Active(true).build();

    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.device);
    when(this.dlmsConnectionManagerMock.getDlmsMessageListener())
        .thenReturn(this.messageListenerMock);
    when(this.throttlingClientConfig.clientEnabled()).thenReturn(false);
    doNothing()
        .when(this.connectionHelper)
        .createAndHandleConnectionForDevice(
            any(MessageMetadata.class),
            same(this.device),
            nullable(DlmsMessageListener.class),
            any(Consumer.class));
  }

  @Test
  void processMessageTaskShouldSendFirmwareFileRequestWhenFirmwareFileNotAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);

    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(false);

    // Act
    this.processor.processMessageTasks(message.getObject(), messageMetadata, null, this.device);

    // Assert
    verify(this.osgpRequestMessageSender, times(1))
        .sendWithReplyToThisInstance(
            any(RequestMessage.class), any(String.class), any(MessageMetadata.class));
  }

  @Test
  void processMessageTaskShouldNotSendFirmwareFileRequestWhenFirmwareFileAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(true);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock, this.device);

    // Assert
    verify(this.osgpRequestMessageSender, never())
        .sendWithReplyToThisInstance(
            any(RequestMessage.class), any(String.class), any(MessageMetadata.class));
  }

  @Test
  void processMessageTaskShouldUpdateFirmwareWhenFirmwareFileAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "available";
    final String deviceIdentification = "availableToo";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(true);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock, this.device);

    // Assert
    verify(this.configurationService, times(1))
        .updateFirmware(
            nullable(DlmsConnectionManager.class),
            same(this.device),
            same(updateFirmwareRequestDto),
            any(MessageMetadata.class));
  }

  @Test
  void processMessageTaskShouldNotUpdateFirmwareWhenFirmwareFileNotAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(false);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock, this.device);

    // Assert
    verify(this.firmwareService, times(0))
        .updateFirmware(
            nullable(DlmsConnectionManager.class),
            same(this.device),
            same(updateFirmwareRequestDto),
            any(MessageMetadata.class));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testUsesDeviceConnection(final boolean isFirmwareFileAvailable) {
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));

    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification))
        .thenReturn(isFirmwareFileAvailable);

    assertThat(this.processor.usesDeviceConnection(updateFirmwareRequestDto))
        .isEqualTo(isFirmwareFileAvailable);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testProcessMessage(final boolean isFirmwareFileAvailable) throws JMSException {
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();

    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification))
        .thenReturn(isFirmwareFileAvailable);

    this.processor.processMessage(message);
  }
}
