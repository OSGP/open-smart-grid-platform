//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class DefinableLoadProfileConfigurationDataFactory {

  public static DefinableLoadProfileConfigurationData fromParameterMap(
      final Map<String, String> parameters) {
    final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData =
        new DefinableLoadProfileConfigurationData();
    definableLoadProfileConfigurationData.setCaptureObjects(
        CaptureObjectsFactory.fromParameterMap(parameters));
    definableLoadProfileConfigurationData.setCapturePeriod(
        getLong(parameters, PlatformSmartmeteringKeys.CAPTURE_PERIOD, null));
    return definableLoadProfileConfigurationData;
  }
}
