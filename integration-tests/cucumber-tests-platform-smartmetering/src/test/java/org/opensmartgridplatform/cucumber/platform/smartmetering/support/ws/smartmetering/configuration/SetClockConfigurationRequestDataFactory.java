/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getHexDecoded;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetClockConfigurationRequestDataFactory {

  public static SetClockConfigurationRequestData fromParameterMap(
      final Map<String, String> parameters) {
    final SetClockConfigurationRequestData setClockConfigurationData =
        new SetClockConfigurationRequestData();
    setClockConfigurationData.setDaylightSavingsBegin(
        getHexDecoded(
            parameters,
            PlatformSmartmeteringKeys.DAYLIGHT_SAVINGS_BEGIN,
            PlatformSmartmeteringDefaults.DAYLIGHT_SAVINGS_BEGIN));
    setClockConfigurationData.setDaylightSavingsEnd(
        getHexDecoded(
            parameters,
            PlatformSmartmeteringKeys.DAYLIGHT_SAVINGS_END,
            PlatformSmartmeteringDefaults.DAYLIGHT_SAVINGS_END));
    setClockConfigurationData.setDaylightSavingsEnabled(
        getBoolean(
            parameters,
            PlatformSmartmeteringKeys.DAYLIGHT_SAVINGS_ENABLED,
            PlatformSmartmeteringDefaults.DAYLIGHT_SAVINGS_ENABLED));
    setClockConfigurationData.setTimeZoneOffset(
        getShort(
            parameters,
            PlatformSmartmeteringKeys.TIME_ZONE_OFFSET,
            PlatformSmartmeteringDefaults.TIME_ZONE_OFFSET));

    return setClockConfigurationData;
  }
}
