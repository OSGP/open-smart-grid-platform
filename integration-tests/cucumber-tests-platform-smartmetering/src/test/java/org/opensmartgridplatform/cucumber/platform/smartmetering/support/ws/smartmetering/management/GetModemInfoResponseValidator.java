/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.BitErrorRate;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.CircuitSwitchedStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ModemRegistrationStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.PacketSwitchedStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SignalQuality;

public class GetModemInfoResponseValidator {
  private GetModemInfoResponseValidator() {
    // Private constructor for utility class
  }

  public static void validate(
      final GetModemInfoResponseData response, final Map<String, String> expectedValues) {
    for (final Map.Entry<String, String> expectedEntyValue : expectedValues.entrySet()) {
      final String expectedValue = expectedEntyValue.getValue();
      switch (expectedEntyValue.getKey()) {
        case "operator":
          assertThat(response.getOperator()).isEqualTo(expectedValue);
          break;
        case "modemRegistrationStatus":
          assertThat(response.getModemRegistrationStatus())
              .isEqualTo(ModemRegistrationStatus.fromValue(expectedValue));
          break;
        case "circuitSwitchedStatus":
          assertThat(response.getCircuitSwitchedStatus())
              .isEqualTo(CircuitSwitchedStatus.fromValue(expectedValue));
          break;
        case "packetSwitchedStatus":
          assertThat(response.getPacketSwitchedStatus())
              .isEqualTo(PacketSwitchedStatus.fromValue(expectedValue));
          break;
        case "cellId":
          assertThat(response.getCellId()).isEqualTo(expectedValue.getBytes());
          break;
        case "locationId":
          assertThat(response.getLocationId()).isEqualTo(expectedValue.getBytes());
          break;
        case "signalQuality":
          assertThat(response.getSignalQuality()).isEqualTo(SignalQuality.fromValue(expectedValue));
          break;
        case "bitErrorRate":
          assertThat(response.getBitErrorRate()).isEqualTo(BitErrorRate.fromValue(expectedValue));
          break;
        case "mobileCountryCode":
          assertThat(response.getMobileCountryCode()).isEqualTo(Long.parseLong(expectedValue));
          break;
        case "mobileNetworkCode":
          assertThat(response.getMobileNetworkCode()).isEqualTo(Long.parseLong(expectedValue));
          break;
        case "channelNumber":
          assertThat(response.getChannelNumber()).isEqualTo(Long.parseLong(expectedValue));
          break;
        case "numberOfAdjacentCells":
          assertThat(response.getNumberOfAdjacentCells()).isEqualTo(Long.parseLong(expectedValue));
          break;
        case "adjacentCellId":
          assertThat(response.getAdjacentCellId()).isEqualTo(expectedValue.getBytes());
          break;
        case "adjacentCellSignalQuality":
          assertThat(response.getAdjacentCellSignalQuality())
              .isEqualTo(SignalQuality.fromValue(expectedValue));
          break;
        case "captureTime":
          try {
            assertThat(response.getCaptureTime())
                .isEqualTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(expectedValue));
          } catch (final DatatypeConfigurationException e) {
            fail("Error while validating capture time :", e);
          }
          break;
        default:
          fail("Unexpected value: " + expectedEntyValue.getKey());
      }
    }
  }
}
