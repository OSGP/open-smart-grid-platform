// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetMbusUserKeyByChannelRequestDataFactory {
  private SetMbusUserKeyByChannelRequestDataFactory() {
    // Private constructor for utility class
  }

  public static SetMbusUserKeyByChannelRequestData fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData =
        new SetMbusUserKeyByChannelRequestData();
    if (!requestParameters.containsKey(PlatformSmartmeteringKeys.CHANNEL)) {
      throw new AssertionError(
          "A value for key '"
              + PlatformSmartmeteringKeys.CHANNEL
              + "' must be set in the step data.");
    }
    setMbusUserKeyByChannelRequestData.setChannel(
        getShort(requestParameters, PlatformSmartmeteringKeys.CHANNEL));
    return setMbusUserKeyByChannelRequestData;
  }
}
