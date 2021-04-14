/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
