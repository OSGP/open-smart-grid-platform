/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RetryHeaderFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UpdateFirmwareRequestMessageProcessorTest {
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

  private DlmsDevice device;

  @InjectMocks private UpdateFirmwareRequestMessageProcessor processor;

  @BeforeEach
  public void setup() throws OsgpException {

    this.device = new DlmsDeviceBuilder().withHls5Active(true).build();

    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.device);
    when(this.dlmsConnectionManagerMock.getDlmsMessageListener())
        .thenReturn(this.messageListenerMock);
    doNothing()
        .when(this.connectionHelper)
        .createAndHandleConnectionForDevice(
            any(MessageMetadata.class),
            same(this.device),
            nullable(DlmsMessageListener.class),
            any(Consumer.class));
  }

  @Test
  public void processMessageTaskShouldSendFirmwareFileRequestWhenFirmwareFileNotAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);

    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(false);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock);

    // Assert
    verify(this.osgpRequestMessageSender, times(1))
        .send(any(RequestMessage.class), any(String.class), any(MessageMetadata.class));
  }

  @Test
  public void processMessageTaskShouldNotSendFirmwareFileRequestWhenFirmwareFileAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(true);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock);

    // Assert
    verify(this.osgpRequestMessageSender, never())
        .send(any(RequestMessage.class), any(String.class), any(MessageMetadata.class));
  }

  @Test
  public void processMessageTaskShouldUpdateFirmwareWhenFirmwareFileAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "available";
    final String deviceIdentification = "availableToo";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(true);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock);

    // Assert
    verify(this.configurationService, times(1))
        .updateFirmware(
            nullable(DlmsConnectionManager.class),
            same(this.device),
            same(updateFirmwareRequestDto),
            any(MessageMetadata.class));
  }

  @Test
  public void processMessageTaskShouldNotUpdateFirmwareWhenFirmwareFileNotAvailable()
      throws JMSException, OsgpException {
    // Arrange
    final String firmwareIdentification = "unavailable";
    final String deviceIdentification = "unavailableEither";
    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withObject(updateFirmwareRequestDto)
            .withCorrelationUid("123456")
            .build();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(false);

    // Act
    this.processor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock);

    // Assert
    verify(this.firmwareService, times(0))
        .updateFirmware(
            nullable(DlmsConnectionManager.class),
            same(this.device),
            same(updateFirmwareRequestDto),
            any(MessageMetadata.class));
  }
}
