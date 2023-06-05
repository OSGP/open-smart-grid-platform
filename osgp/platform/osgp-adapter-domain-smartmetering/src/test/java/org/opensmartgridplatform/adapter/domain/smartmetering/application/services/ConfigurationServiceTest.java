// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SecretType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {

  private static final String DEVICE_IDENTIFICATION = "test-device-identification";
  private static final String ORGANISATION_IDENTIFICATION = "organisation";
  private static final String CORRELATION_UID = "123";
  private static final String MESSAGE_TYPE = "message-type";
  private static final int PRIORITY = 1;
  private static final Long SCHEDULE_TIME = System.currentTimeMillis();
  private static final Long MAX_SCHEDULE_TIME = SCHEDULE_TIME + 10000;
  private static final boolean BYPASS_RETRY = true;
  private static final byte[] KEY_1 = {1, 2, 3};
  private static final byte[] KEY_2 = {4, 5, 6};
  private static final MessageMetadata messageMetadata =
      MessageMetadata.newBuilder()
          .withDeviceIdentification(DEVICE_IDENTIFICATION)
          .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
          .withCorrelationUid(CORRELATION_UID)
          .withMessageType(MESSAGE_TYPE)
          .withMessagePriority(PRIORITY)
          .withScheduleTime(SCHEDULE_TIME)
          .withMaxScheduleTime(MAX_SCHEDULE_TIME)
          .withBypassRetry(BYPASS_RETRY)
          .build();
  private static final SmartMeter device =
      new SmartMeter(DEVICE_IDENTIFICATION, "Alias", new Address(), new GpsCoordinates(10f, 15f));
  private static final GetKeysRequestData getKeysRequestData =
      new GetKeysRequestData(
          Arrays.asList(SecretType.E_METER_AUTHENTICATION_KEY, SecretType.E_METER_MASTER_KEY));
  private static final GetKeysResponseDto getKeysResponseDto =
      new GetKeysResponseDto(
          Arrays.asList(
              new KeyDto(SecretTypeDto.E_METER_AUTHENTICATION_KEY, KEY_1),
              new KeyDto(SecretTypeDto.E_METER_MASTER_KEY, KEY_2)));

  private static final String IP_ADDRESS;
  private static final Integer BASE_TRANSCEIVER_STATION_ID;
  private static final Integer CELL_ID;

  static {
    try {
      device.setNetworkAddress(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}));
      IP_ADDRESS = device.getIpAddress();
      BASE_TRANSCEIVER_STATION_ID = device.getBtsId();
      CELL_ID = device.getCellId();
    } catch (final UnknownHostException e) {
      throw new AssertionError(e);
    }
  }

  @InjectMocks private ConfigurationService instance;

  @Mock private DomainHelperService domainHelperService;
  @Mock private JmsMessageSender osgpCoreRequestMessageSender;
  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Captor private ArgumentCaptor<MessageMetadata> messageMetadataCaptor;
  @Captor private ArgumentCaptor<Serializable> requestMessageCaptor;
  @Captor private ArgumentCaptor<ResponseMessage> responseMessageCaptor;

  @Test
  void getKeys() throws FunctionalException {

    // SETUP
    when(this.domainHelperService.findSmartMeter(DEVICE_IDENTIFICATION)).thenReturn(device);

    // CALL
    this.instance.getKeys(messageMetadata, getKeysRequestData);

    // VERIFY
    final GetKeysRequestDto expectedRequestDto =
        new GetKeysRequestDto(
            Arrays.asList(
                SecretTypeDto.E_METER_AUTHENTICATION_KEY, SecretTypeDto.E_METER_MASTER_KEY));
    final MessageMetadata expectedMessageMetadata =
        messageMetadata
            .builder()
            .withIpAddress(IP_ADDRESS)
            .withNetworkSegmentIds(BASE_TRANSCEIVER_STATION_ID, CELL_ID)
            .build();

    verify(this.osgpCoreRequestMessageSender)
        .send(this.requestMessageCaptor.capture(), this.messageMetadataCaptor.capture());

    assertThat(this.requestMessageCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedRequestDto);

    assertThat(this.messageMetadataCaptor.getValue())
        .isEqualToComparingFieldByField(expectedMessageMetadata);
  }

  @Test
  void handleGetKeysResponse() {

    // CALL
    this.instance.handleGetKeysResponse(
        messageMetadata, ResponseMessageResultType.OK, null, getKeysResponseDto);

    // VERIFY
    final GetKeysResponse expectedGetKeysResponse =
        new GetKeysResponse(
            Arrays.asList(
                new GetKeysResponseData(SecretType.E_METER_AUTHENTICATION_KEY, KEY_1),
                new GetKeysResponseData(SecretType.E_METER_MASTER_KEY, KEY_2)));
    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withDataObject(expectedGetKeysResponse)
            .withResult(ResponseMessageResultType.OK)
            .build();

    verify(this.webServiceResponseMessageSender)
        .send(this.responseMessageCaptor.capture(), eq(MESSAGE_TYPE));

    assertThat(this.responseMessageCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedResponseMessage);
  }
}
