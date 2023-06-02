//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeyOnGMeterRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeyOnGMeterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@ExtendWith(MockitoExtension.class)
class SetKeyOnGMeterDataConverterTest {
  @Mock private DomainHelperService domainHelperService;
  @Mock private SmartMeter smartMeter;

  @InjectMocks private SetKeyOnGMeterDataConverter setKeyOnGMeterDataConverter;

  @Test
  void convertTest() throws FunctionalException {
    final String mbusDeviceIdentification = "GTEST123";
    final SecretType secretType = SecretType.G_METER_OPTICAL_PORT_KEY;
    final Boolean closeOpticalPort = true;
    final Device gatewayDevice = new Device("AB456");

    when(this.domainHelperService.findSmartMeter(any())).thenReturn(this.smartMeter);
    when(this.smartMeter.getGatewayDevice()).thenReturn(gatewayDevice);

    final SetKeyOnGMeterRequestData data =
        new SetKeyOnGMeterRequestData(mbusDeviceIdentification, secretType, closeOpticalPort);

    final SetKeyOnGMeterRequestDto convertedData =
        this.setKeyOnGMeterDataConverter.convert(data, new SmartMeter());

    assertThat(convertedData).isNotNull();
    assertThat(convertedData.getSecretType())
        .isEqualTo(SecretTypeDto.values()[secretType.ordinal()]);
    assertThat(convertedData.getCloseOpticalPort()).isEqualTo(closeOpticalPort);
  }
}
