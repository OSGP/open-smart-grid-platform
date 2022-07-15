/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.DomainHelperService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SecretType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetEncryptionKeyExchangeOnGMeterRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetEncryptionKeyExchangeOnGMeterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@ExtendWith(MockitoExtension.class)
class SetEncryptionKeyExchangeOnGMeterDataConverterTest {
  @Mock private DomainHelperService domainHelperService;
  @Mock private SmartMeter smartMeter;

  @InjectMocks
  private SetEncryptionKeyExchangeOnGMeterDataConverter
      setEncryptionKeyExchangeOnGMeterDataConverter;

  @Test
  void convertTest() throws FunctionalException {
    final String mbusDeviceIdentification = "GTEST123";
    final SecretType secretType = SecretType.G_METER_OPTICAL_PORT_KEY;
    final Boolean closeOpticalPort = true;
    final Device gatewayDevice = new Device("AB456");

    when(this.domainHelperService.findSmartMeter(any())).thenReturn(this.smartMeter);
    when(this.smartMeter.getGatewayDevice()).thenReturn(gatewayDevice);

    final SetEncryptionKeyExchangeOnGMeterRequestData data =
        new SetEncryptionKeyExchangeOnGMeterRequestData(
            mbusDeviceIdentification, secretType, closeOpticalPort);

    final SetEncryptionKeyExchangeOnGMeterRequestDto convertedData =
        this.setEncryptionKeyExchangeOnGMeterDataConverter.convert(data, new SmartMeter());

    assertThat(convertedData).isNotNull();
    assertThat(convertedData.getSecretType())
        .isEqualTo(SecretTypeDto.values()[secretType.ordinal()]);
    assertThat(convertedData.getCloseOpticalPort()).isEqualTo(closeOpticalPort);
  }
}
