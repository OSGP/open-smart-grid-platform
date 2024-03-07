// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.throttling.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RetryHeaderFactory;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
class GetFirmwareFileResponseMessageProcessorTest {
  @Mock protected DlmsConnectionHelper connectionHelper;

  @Mock protected DeviceResponseMessageSender responseMessageSender;

  @Mock protected DomainHelperService domainHelperService;

  @Mock protected DlmsMessageListener dlmsMessageListenerMock;

  @Mock private RetryHeaderFactory retryHeaderFactory;

  @Mock private FirmwareService firmwareService;

  @Mock private DlmsConnectionManager dlmsConnectionManagerMock;

  @Mock private ThrottlingService throttlingService;

  @Mock private ThrottlingClientConfig throttlingClientConfig;

  @Mock private OsgpExceptionConverter osgpExceptionConverter;

  private DlmsDevice dlmsDevice;

  @InjectMocks
  private GetFirmwareFileResponseMessageProcessor getFirmwareFileResponseMessageProcessor;

  @BeforeEach
  void setUp() {
    this.dlmsDevice = new DlmsDeviceBuilder().withHls5Active(true).build();
    lenient().when(this.throttlingClientConfig.clientEnabled()).thenReturn(false);
  }

  @Test
  void processMessageShouldCallConnectionHelperCreateAndHandleConnection()
      throws JMSException, OsgpException {
    final FirmwareFileDto firmwareFileDto = this.setupFirmwareFileDto();
    final ResponseMessage responseMessage = this.setupResponseMessage(firmwareFileDto);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withMessageType(MessageType.GET_FIRMWARE_FILE.name())
            .withObject(responseMessage)
            .build();

    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.dlmsDevice);

    this.getFirmwareFileResponseMessageProcessor.processMessage(message);

    verify(this.connectionHelper)
        .createAndHandleConnectionForDevice(
            any(MessageMetadata.class), eq(this.dlmsDevice), isNull(), isNull(), any());
  }

  @Test
  void processMessageShouldSendOkResponseMessageContainingFirmwareVersions()
      throws OsgpException, JMSException {
    // arrange
    final FirmwareFileDto firmwareFileDto = this.setupFirmwareFileDto();
    final ResponseMessage responseMessage = this.setupResponseMessage(firmwareFileDto);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withMessageType(MessageType.GET_FIRMWARE_FILE.name())
            .withObject(responseMessage)
            .build();
    final UpdateFirmwareResponseDto updateFirmwareResponseDto =
        new UpdateFirmwareResponseDto(firmwareFileDto.getFirmwareIdentification());

    final ArgumentCaptor<ResponseMessage> responseMessageArgumentCaptor =
        ArgumentCaptor.forClass(ResponseMessage.class);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder(MessageMetadata.fromMessage(message))
            .withMessageType(MessageType.UPDATE_FIRMWARE.name())
            .build();

    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.dlmsDevice);
    when(this.dlmsConnectionManagerMock.getDlmsMessageListener())
        .thenReturn(this.dlmsMessageListenerMock);
    when(this.firmwareService.updateFirmware(
            same(this.dlmsConnectionManagerMock),
            same(this.dlmsDevice),
            same(firmwareFileDto),
            any(MessageMetadata.class)))
        .thenReturn(updateFirmwareResponseDto);

    // act
    this.getFirmwareFileResponseMessageProcessor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock);

    // assert
    verify(this.responseMessageSender, times(1)).send(responseMessageArgumentCaptor.capture());

    assertThat(responseMessageArgumentCaptor.getValue().getDataObject())
        .isSameAs(updateFirmwareResponseDto);
    assertThat(responseMessageArgumentCaptor.getValue().getResult())
        .isSameAs(ResponseMessageResultType.OK);
  }

  @Test
  void createMessageShouldCallUpdateFirmware() throws OsgpException {
    // arrange
    final FirmwareFileDto firmwareFileDto = this.setupFirmwareFileDto();
    final ResponseMessage responseMessage = this.setupResponseMessage(firmwareFileDto);

    // act
    this.getFirmwareFileResponseMessageProcessor.handleMessage(
        this.dlmsConnectionManagerMock, this.dlmsDevice, responseMessage);

    // assert
    verify(this.firmwareService, times(1))
        .updateFirmware(
            same(this.dlmsConnectionManagerMock),
            same(this.dlmsDevice),
            same(firmwareFileDto),
            any(MessageMetadata.class));
  }

  @Test
  void processMessageShouldSendNotOkResponseMessageContainingOriginalFirmwareUpdateRequest()
      throws OsgpException, JMSException {
    // arrange
    final FirmwareFileDto firmwareFileDto = this.setupFirmwareFileDto();
    final ResponseMessage responseMessage = this.setupResponseMessage(firmwareFileDto);
    final ObjectMessage message =
        new ObjectMessageBuilder()
            .withMessageType(MessageType.GET_FIRMWARE_FILE.name())
            .withObject(responseMessage)
            .build();
    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder(MessageMetadata.fromMessage(message))
            .withMessageType(MessageType.UPDATE_FIRMWARE.name())
            .build();

    final ArgumentCaptor<ResponseMessage> responseMessageArgumentCaptor =
        ArgumentCaptor.forClass(ResponseMessage.class);

    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.dlmsDevice);
    when(this.dlmsConnectionManagerMock.getDlmsMessageListener())
        .thenReturn(this.dlmsMessageListenerMock);
    when(this.firmwareService.updateFirmware(
            same(this.dlmsConnectionManagerMock),
            same(this.dlmsDevice),
            same(firmwareFileDto),
            any(MessageMetadata.class)))
        .thenThrow(new ProtocolAdapterException("Firmware file fw is not available."));

    // act
    this.getFirmwareFileResponseMessageProcessor.processMessageTasks(
        message.getObject(), messageMetadata, this.dlmsConnectionManagerMock);

    // assert
    verify(this.responseMessageSender, times(1)).send(responseMessageArgumentCaptor.capture());

    final ResponseMessage capturedValue = responseMessageArgumentCaptor.getValue();
    assertThat(
            ((UpdateFirmwareRequestDto) capturedValue.getDataObject()).getFirmwareIdentification())
        .isSameAs(firmwareFileDto.getFirmwareIdentification());
    assertThat(capturedValue.getResult()).isSameAs(ResponseMessageResultType.NOT_OK);
    assertThat(capturedValue.bypassRetry()).isFalse();
  }

  private FirmwareFileDto setupFirmwareFileDto() {
    return new FirmwareFileDto(
        "fw",
        "device-1",
        "fw".getBytes(StandardCharsets.UTF_8),
        Hex.decode("496d6167654964656e746966696572"));
  }

  private ProtocolResponseMessage setupResponseMessage(final FirmwareFileDto firmwareFileDto) {
    return ProtocolResponseMessage.newBuilder()
        .correlationUid("corr-uid-1")
        .result(ResponseMessageResultType.OK)
        .dataObject(firmwareFileDto)
        .build();
  }
}
