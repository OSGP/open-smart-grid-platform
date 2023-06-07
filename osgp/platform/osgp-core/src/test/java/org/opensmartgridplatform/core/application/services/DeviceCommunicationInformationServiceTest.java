/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType.NOT_OK;
import static org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType.OK;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
public class DeviceCommunicationInformationServiceTest {

  @Mock private DeviceRepository deviceRepository;

  @InjectMocks private DeviceCommunicationInformationService deviceCommunicationInformationService;

  @ParameterizedTest
  @EnumSource(ResponseMessageResultType.class)
  public void updateDeviceConnectionInformation(final ResponseMessageResultType resultType) {
    final String deviceIdentification = "device-id1";

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder().withDeviceIdentification(deviceIdentification).build();
    final ProtocolResponseMessage message =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(messageMetadata.builder().build())
            .result(resultType)
            .build();

    this.deviceCommunicationInformationService.updateDeviceConnectionInformation(message);

    verify(this.deviceRepository, times(resultType == OK ? 1 : 0))
        .updateConnectionDetailsToSuccess(deviceIdentification);
    verify(this.deviceRepository, times(resultType == NOT_OK ? 1 : 0))
        .updateConnectionDetailsToFailure(deviceIdentification);
  }
}
