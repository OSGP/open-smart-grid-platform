/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
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
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {

  private static final String DEVICE_IDENTIFICATION = "test-device-identification";
  private static final String ORGANISATION_IDENTIFICATION = "organisation";
  private static final String CORRELATION_UID = "123";
  private static final String MESSAGE_TYPE = "message-type";
  private static final int PRIORITY = 1;
  private static final Long SCHEDULE_TIME = 1000L;
  private static final boolean BYPASS_RETRY = true;
  private static final byte[] KEY_1 = new byte[] {1, 2, 3};
  private static final byte[] KEY_2 = new byte[] {4, 5, 6};
  private static final MessageMetadata deviceMessageMetadata =
      new MessageMetadata.Builder()
          .withDeviceIdentification(DEVICE_IDENTIFICATION)
          .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
          .withCorrelationUid(CORRELATION_UID)
          .withMessageType(MESSAGE_TYPE)
          .withMessagePriority(PRIORITY)
          .withScheduleTime(SCHEDULE_TIME)
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

  @InjectMocks private ConfigurationService instance;

  @Mock private DomainHelperService domainHelperService;
  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Captor private ArgumentCaptor<RequestMessage> requestMessageCaptor;
  @Captor private ArgumentCaptor<ResponseMessage> responseMessageCaptor;

  @Test
  void getKeys() throws FunctionalException {

    // SETUP
    when(this.domainHelperService.findSmartMeter(DEVICE_IDENTIFICATION)).thenReturn(device);

    // CALL
    this.instance.getKeys(deviceMessageMetadata, getKeysRequestData);

    // VERIFY
    final GetKeysRequestDto expectedGetKeysRequestDto =
        new GetKeysRequestDto(
            Arrays.asList(
                SecretTypeDto.E_METER_AUTHENTICATION_KEY, SecretTypeDto.E_METER_MASTER_KEY));
    final RequestMessage expectedRequestMessage =
        new RequestMessage(
            CORRELATION_UID,
            ORGANISATION_IDENTIFICATION,
            DEVICE_IDENTIFICATION,
            null,
            expectedGetKeysRequestDto);

    verify(this.osgpCoreRequestMessageSender)
        .send(
            this.requestMessageCaptor.capture(),
            eq(MESSAGE_TYPE),
            eq(PRIORITY),
            eq(SCHEDULE_TIME),
            eq(BYPASS_RETRY));

    assertThat(this.requestMessageCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedRequestMessage);
  }

  @Test
  void handleGetKeysResponse() {

    // CALL
    this.instance.handleGetKeysResponse(
        deviceMessageMetadata, ResponseMessageResultType.OK, null, getKeysResponseDto);

    // VERIFY
    final GetKeysResponse expectedGetKeysResponse =
        new GetKeysResponse(
            Arrays.asList(
                new GetKeysResponseData(SecretType.E_METER_AUTHENTICATION_KEY, KEY_1),
                new GetKeysResponseData(SecretType.E_METER_MASTER_KEY, KEY_2)));
    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(CORRELATION_UID)
            .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withMessagePriority(PRIORITY)
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
