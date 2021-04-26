/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BitErrorRateType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CircuitSwitchedStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ModemRegistrationStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PacketSwitchedStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SignalQualityType;

class GetGsmDiagnosticResponseMappingTest {

  private final ManagementMapper managementMapper = new ManagementMapper();

  @Test
  void shouldConvertGetGsmDiagnosticResponse() {
    final GetGsmDiagnosticResponseData source = this.newGetGsmDiagnosticResponseData();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetGsmDiagnosticResponse
        target =
            this.managementMapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .GetGsmDiagnosticResponse.class);

    // Assert
    assertThat(target.getOperator()).isEqualTo(source.getOperator());
    assertThat(target.getModemRegistrationStatus().name())
        .isEqualTo(source.getModemRegistrationStatus().name());
    assertThat(target.getCircuitSwitchedStatus().name())
        .isEqualTo(source.getCircuitSwitchedStatus().name());
    assertThat(target.getPacketSwitchedStatus().name())
        .isEqualTo(source.getPacketSwitchedStatus().name());
    assertThat(target.getCellId()).isEqualTo(source.getCellId());
    assertThat(target.getLocationId()).isEqualTo(source.getLocationId());
    assertThat(target.getSignalQuality().name()).isEqualTo(source.getSignalQuality().name());
    assertThat(target.getBitErrorRate().name()).isEqualTo(source.getBitErrorRate().name());
    assertThat(target.getMobileCountryCode()).isEqualTo(source.getMobileCountryCode());
    assertThat(target.getMobileNetworkCode()).isEqualTo(source.getMobileNetworkCode());
    assertThat(target.getChannelNumber()).isEqualTo(source.getChannelNumber());
    assertThat(target.getNumberOfAdjacentCells()).isEqualTo(source.getNumberOfAdjacentCells());
    assertThat(target.getAdjacentCellId()).isEqualTo(source.getAdjacentCellId());
    assertThat(target.getAdjacentCellSignalQuality().name())
        .isEqualTo(source.getAdjacentCellSignalQuality().name());
    assertThat(target.getCaptureTime().toGregorianCalendar().getTime().getTime())
        .isEqualTo(source.getCaptureTime().getTime());
  }

  private GetGsmDiagnosticResponseData newGetGsmDiagnosticResponseData() {
    return new GetGsmDiagnosticResponseData(
        "operator",
        ModemRegistrationStatusType.REGISTERED_ROAMING,
        CircuitSwitchedStatusType.ACTIVE,
        PacketSwitchedStatusType.GPRS,
        "cellId".getBytes(),
        "locationId".getBytes(),
        SignalQualityType.MINUS_61_DBM,
        BitErrorRateType.RXQUAL_4,
        31L,
        42L,
        1L,
        2L,
        "adjacentCellId".getBytes(),
        SignalQualityType.MINUS_61_DBM,
        new Date());
  }
}
