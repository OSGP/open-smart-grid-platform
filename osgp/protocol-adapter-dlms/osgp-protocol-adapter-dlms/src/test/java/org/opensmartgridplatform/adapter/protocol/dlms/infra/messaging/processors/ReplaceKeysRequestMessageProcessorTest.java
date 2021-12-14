/*
 * Copyright 2021 Alliander N.V.
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
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
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DeviceKeyProcessingService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReplaceKeysRequestMessageProcessorTest {

  @Mock protected DlmsConnectionHelper connectionHelper;

  @Mock private DomainHelperService domainHelperService;

  @Mock private ConfigurationService configurationService;

  @Mock private DeviceRequestMessageSender deviceRequestMessageSender;

  @Mock private DeviceKeyProcessingService deviceKeyProcessingService;

  @Mock private DlmsConnectionManager dlmsConnectionManagerMock;

  @Mock private DlmsMessageListener messageListenerMock;

  @Mock private ThrottlingService throttlingService;

  @Mock private ThrottlingClientConfig throttlingClientConfig;

  private DlmsDevice device;
  private String deviceIdentification;
  private Duration deviceKeyProcessingTimeout;
  private SetKeysRequestDto messageObject;
  private ObjectMessage message;
  private MessageMetadata messageMetadata;

  @InjectMocks private ReplaceKeysRequestMessageProcessor processor;

  @BeforeEach
  void setup() throws OsgpException, JMSException {

    this.deviceIdentification = "device-1";
    this.device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withDeviceIdentification(this.deviceIdentification)
            .build();
    this.deviceKeyProcessingTimeout = Duration.ofSeconds(300);
    this.messageObject = new SetKeysRequestDto(new byte[0], new byte[0]);
    this.message =
        new ObjectMessageBuilder()
            .withObject(this.messageObject)
            .withCorrelationUid("123456")
            .withMessageType(MessageType.REPLACE_KEYS.name())
            .withDeviceIdentification(this.deviceIdentification)
            .build();
    this.messageMetadata = MessageMetadata.fromMessage(this.message);
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
  void processMessageWhenNoOtherKeyProcessIsRunning()
      throws OsgpException, JMSException, DeviceKeyProcessAlreadyRunningException {

    // Arrange
    when(this.deviceKeyProcessingService.getDeviceKeyProcessingTimeout())
        .thenReturn(this.deviceKeyProcessingTimeout);

    // Act
    this.processor.processMessage(this.message);

    // Assert
    verify(this.deviceKeyProcessingService).startProcessing(this.deviceIdentification);
    verify(this.deviceRequestMessageSender, never())
        .send(this.messageObject, this.messageMetadata, this.deviceKeyProcessingTimeout);
  }

  @Test
  void processMessageWhenAnotherKeyProcessIsAlreadyRunning()
      throws OsgpException, JMSException, DeviceKeyProcessAlreadyRunningException {

    // Arrange
    doThrow(new DeviceKeyProcessAlreadyRunningException())
        .when(this.deviceKeyProcessingService)
        .startProcessing(this.deviceIdentification);
    when(this.deviceKeyProcessingService.getDeviceKeyProcessingTimeout())
        .thenReturn(this.deviceKeyProcessingTimeout);

    // Act
    this.processor.processMessage(this.message);

    // Assert
    verify(this.deviceRequestMessageSender)
        .send(
            same(this.messageObject),
            any(MessageMetadata.class),
            same(this.deviceKeyProcessingTimeout));
  }
}
