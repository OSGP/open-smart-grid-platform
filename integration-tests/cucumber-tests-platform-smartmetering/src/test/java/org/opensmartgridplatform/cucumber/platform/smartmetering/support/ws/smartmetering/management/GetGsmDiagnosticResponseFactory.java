//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetGsmDiagnosticResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.AdjacentCellInfo;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.BitErrorRate;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.CellInfo;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.CircuitSwitchedStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ModemRegistrationStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.PacketSwitchedStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SignalQuality;

public class GetGsmDiagnosticResponseFactory {

  private GetGsmDiagnosticResponseFactory() {
    // Private constructor for utility class
  }

  public static GetGsmDiagnosticResponse fromParameterMap(
      final Map<String, String> requestParameters) {

    final String operator = getString(requestParameters, "operator");
    final ModemRegistrationStatus modemRegistrationStatus =
        getEnum(requestParameters, "modemRegistrationStatus", ModemRegistrationStatus.class);
    final CircuitSwitchedStatus circuitSwitchedStatus =
        getEnum(requestParameters, "circuitSwitchedStatus", CircuitSwitchedStatus.class);
    final PacketSwitchedStatus packetSwitchedStatus =
        getEnum(requestParameters, "packetSwitchedStatus", PacketSwitchedStatus.class);

    final Long cellId = getLong(requestParameters, "cellId");
    final Integer locationId = getInteger(requestParameters, "locationId");
    final SignalQuality signalQuality =
        getEnum(requestParameters, "signalQuality", SignalQuality.class);
    final BitErrorRate bitErrorRate =
        getEnum(requestParameters, "bitErrorRate", BitErrorRate.class);
    final Integer mobileCountryCode = getInteger(requestParameters, "mobileCountryCode");
    final Integer mobileNetworkCode = getInteger(requestParameters, "mobileNetworkCode");
    final Long channelNumber = getLong(requestParameters, "channelNumber");

    final CellInfo cellInfo = new CellInfo();
    cellInfo.setCellId(cellId);
    cellInfo.setLocationId(locationId);
    cellInfo.setSignalQuality(signalQuality);
    cellInfo.setBitErrorRate(bitErrorRate);
    cellInfo.setMobileCountryCode(mobileCountryCode);
    cellInfo.setMobileNetworkCode(mobileNetworkCode);
    cellInfo.setChannelNumber(channelNumber);

    final List<String> adjacentCellIds =
        Arrays.asList(getString(requestParameters, "adjacentCellIds").split(","));
    final List<String> adjacentCellSignalQualities =
        Arrays.asList(getString(requestParameters, "adjacentCellSignalQualities").split(","));

    final List<AdjacentCellInfo> adjacentCellInfos =
        IntStream.range(0, adjacentCellIds.size())
            .mapToObj(
                i -> {
                  final AdjacentCellInfo adjacentCellInfo = new AdjacentCellInfo();
                  adjacentCellInfo.setCellId(Long.parseLong(adjacentCellIds.get(i)));
                  adjacentCellInfo.setSignalQuality(
                      SignalQuality.fromValue(adjacentCellSignalQualities.get(i)));
                  return adjacentCellInfo;
                })
            .collect(Collectors.toList());

    final GetGsmDiagnosticResponse getGsmDiagnosticResponse = new GetGsmDiagnosticResponse();

    getGsmDiagnosticResponse.setOperator(operator);
    getGsmDiagnosticResponse.setModemRegistrationStatus(modemRegistrationStatus);
    getGsmDiagnosticResponse.setCircuitSwitchedStatus(circuitSwitchedStatus);
    getGsmDiagnosticResponse.setPacketSwitchedStatus(packetSwitchedStatus);
    getGsmDiagnosticResponse.setCellInfo(cellInfo);
    getGsmDiagnosticResponse.getAdjacentCells().addAll(adjacentCellInfos);

    return getGsmDiagnosticResponse;
  }
}
