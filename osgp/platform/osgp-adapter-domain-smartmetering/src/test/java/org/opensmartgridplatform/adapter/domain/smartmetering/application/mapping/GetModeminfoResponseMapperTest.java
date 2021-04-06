/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BitErrorRateType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CircuitSwitchedStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetModemInfoResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ModemRegistrationStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PacketSwitchedStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SignalQualityType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;

class GetModeminfoResponseMapperTest {

  private final ManagementMapper managementMapper = new ManagementMapper();

  @Test
  void shouldConvertGetModemInfoResponse() {
    final GetModemInfoResponseDto source = this.makeResponse();
    final GetModemInfoResponseData target = this.managementMapper.map(source,
        GetModemInfoResponseData.class);

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
    assertThat(target.getAdjacantCellId()).isEqualTo(source.getAdjacantCellId());
    assertThat(target.getAdjacantCellSignalQuality().name())
        .isEqualTo(source.getAdjacantCellSignalQuality().name());
    assertThat(target.getCaptureTime().getTime())
        .isEqualTo(source.getCaptureTime().getTime());
  }

  private GetModemInfoResponseDto makeResponse() {
    return new GetModemInfoResponseDto(
        "operator",
        ModemRegistrationStatusDto.REGISTERED_ROAMING,
        CircuitSwitchedStatusDto.ACTIVE,
        PacketSwitchedStatusDto.GPRS,
        "cellId".getBytes(),
        "locationId".getBytes(),
        SignalQualityDto.MINUS_61_DBM,
        BitErrorRateDto.RXQUAL_4,
        31L,
        42L,
        1L,
        2L,
        "adjacantCellId".getBytes(),
        SignalQualityDto.MINUS_61_DBM,
        new Date());
  }
}
