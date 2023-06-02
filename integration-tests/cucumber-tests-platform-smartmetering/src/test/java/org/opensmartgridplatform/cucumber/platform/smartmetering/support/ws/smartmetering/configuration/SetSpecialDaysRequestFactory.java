//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetSpecialDaysRequestFactory {
  private SetSpecialDaysRequestFactory() {
    // Private constructor for utility class
  }

  public static SetSpecialDaysRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
    setSpecialDaysRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    setSpecialDaysRequest.setSpecialDaysRequestData(fetchSpecialDays());

    return setSpecialDaysRequest;
  }

  public static SetSpecialDaysAsyncRequest fromScenarioContext() {
    final SetSpecialDaysAsyncRequest setSpecialDaysAsyncRequest = new SetSpecialDaysAsyncRequest();
    setSpecialDaysAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setSpecialDaysAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return setSpecialDaysAsyncRequest;
  }

  private static SpecialDaysRequestData fetchSpecialDays() {
    /**
     * 2 bytes for year (century byte and year byte, 0xFFFF = undefined). 1 for month, 0xFF
     * (undefined), 0xFD (end daylight saving), 0xFE (begin daylight saving). 1 for day of month,
     * 0xFF (undefined), 0xFD (2nd last day of month), 0xFE (last day of month). 1 for day of week,
     * 1 is monday, 0xFF (undefined)
     */

    // last Sunday in every year and month
    final byte[] specialDayDateByteArray =
        new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, 0x07};

    final SpecialDay specialDay = new SpecialDay();
    specialDay.setDayId(1);
    specialDay.setSpecialDayDate(specialDayDateByteArray);

    final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
    final List<SpecialDay> specialDays = new ArrayList<>();
    specialDays.add(specialDay);

    specialDaysRequestData.getSpecialDays().addAll(specialDays);

    return specialDaysRequestData;
  }
}
