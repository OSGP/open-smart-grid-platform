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
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getByte;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SynchronizeTimeRequestDataFactory {

  public static SynchronizeTimeRequestData fromParameterMap(final Map<String, String> parameters) {
    final SynchronizeTimeRequestData synchronizeTimeRequestData = new SynchronizeTimeRequestData();

    synchronizeTimeRequestData.setDeviation(
        getByte(
            parameters,
            PlatformSmartmeteringKeys.DEVIATION,
            PlatformSmartmeteringDefaults.DEVIATION));
    synchronizeTimeRequestData.setDst(
        getBoolean(
            parameters,
            PlatformSmartmeteringKeys.DAYLIGHT_SAVINGS_ACTIVE,
            PlatformSmartmeteringDefaults.DAYLIGHT_SAVINGS_ACTIVE));

    return synchronizeTimeRequestData;
  }
}
