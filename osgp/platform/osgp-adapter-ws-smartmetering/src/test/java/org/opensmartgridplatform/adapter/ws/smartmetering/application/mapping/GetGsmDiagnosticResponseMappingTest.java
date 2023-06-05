// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdjacentCellInfo;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BitErrorRateType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CellInfo;
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
    assertThat(target).usingRecursiveComparison().ignoringFields("captureTime").isEqualTo(source);
    assertThat(target.getCaptureTime().toGregorianCalendar().getTime().getTime())
        .isEqualTo(source.getCaptureTime().getTime());
  }

  private GetGsmDiagnosticResponseData newGetGsmDiagnosticResponseData() {
    final CellInfo cellInfo =
        new CellInfo(
            77L, 2230, SignalQualityType.MINUS_61_DBM, BitErrorRateType.RXQUAL_4, 31, 42, 1L);

    final AdjacentCellInfo adjacentCellInfo =
        new AdjacentCellInfo(93L, SignalQualityType.MINUS_61_DBM);

    return new GetGsmDiagnosticResponseData(
        "operator",
        ModemRegistrationStatusType.REGISTERED_ROAMING,
        CircuitSwitchedStatusType.ACTIVE,
        PacketSwitchedStatusType.GPRS,
        cellInfo,
        Collections.singletonList(adjacentCellInfo),
        new Date());
  }
}
