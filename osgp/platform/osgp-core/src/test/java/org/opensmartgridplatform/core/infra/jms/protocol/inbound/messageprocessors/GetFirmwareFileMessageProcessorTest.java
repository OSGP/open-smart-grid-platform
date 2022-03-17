/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.core.infra.jms.protocol.outbound.ProtocolResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetFirmwareFileMessageProcessorTest {

  @Mock private ProtocolResponseMessageSender protocolResponseMessageSender;

  @Mock private DeviceRepository deviceRepository;

  @Mock private FirmwareFileRepository firmwareFileRepository;

  @Mock private Device deviceMock;

  @Mock private FirmwareFile firmwareFileMock;

  @Mock private ActiveMQDestination destinationMock;

  @InjectMocks private GetFirmwareFileMessageProcessor getFirmwareFileMessageProcessor;

  @Test
  public void processMessageShouldSendFirmwareFile() throws JMSException {
    // arrange
    final String correlationUid = "corr-uid-1";
    final String organisationIdentification = "test-org";
    final String deviceIdentification = "dvc-1";

    final String firmwareFileIdentification = "fw";
    final byte[] firmwareFileBytes = firmwareFileIdentification.getBytes();

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareFileIdentification, deviceIdentification);
    final RequestMessage requestMessage =
        new RequestMessage(
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            updateFirmwareRequestDto);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withMessageType(DeviceFunction.GET_FIRMWARE_FILE.name())
            .withDeviceIdentification(deviceIdentification)
            .withObject(requestMessage)
            .build();
    message.setJMSReplyTo(this.destinationMock);

    when(this.deviceMock.getDeviceIdentification()).thenReturn(deviceIdentification);
    when(this.deviceRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(this.deviceMock);

    when(this.firmwareFileMock.getFilename()).thenReturn(firmwareFileIdentification);
    when(this.firmwareFileMock.getFile()).thenReturn(firmwareFileBytes);
    when(this.firmwareFileRepository.findByIdentificationOnly(firmwareFileIdentification))
        .thenReturn(this.firmwareFileMock);

    final byte[] expectedFile = firmwareFileBytes;
    final String expectedMessageType = DeviceFunction.GET_FIRMWARE_FILE.name();

    final ArgumentCaptor<ProtocolResponseMessage> responseMessageArgumentCaptor =
        ArgumentCaptor.forClass(ProtocolResponseMessage.class);
    final ArgumentCaptor<String> messageTypeCaptor = ArgumentCaptor.forClass(String.class);

    // act
    this.getFirmwareFileMessageProcessor.processMessage(message);

    // assert
    verify(this.protocolResponseMessageSender, times(1))
        .sendWithDestination(
            responseMessageArgumentCaptor.capture(),
            messageTypeCaptor.capture(),
            nullable(ProtocolInfo.class),
            any(MessageMetadata.class),
            eq(this.destinationMock));

    final FirmwareFileDto actualFirmwareFileDto =
        (FirmwareFileDto) responseMessageArgumentCaptor.getValue().getDataObject();
    final String actualMessageType = messageTypeCaptor.getValue();

    assertThat(actualFirmwareFileDto.getFirmwareFile()).isEqualTo(expectedFile);
    assertThat(actualFirmwareFileDto.getDeviceIdentification()).isEqualTo(deviceIdentification);
    assertThat(actualFirmwareFileDto.getFirmwareIdentification())
        .isEqualTo(firmwareFileIdentification);
    assertThat(actualMessageType).isEqualTo(expectedMessageType);
  }
}
