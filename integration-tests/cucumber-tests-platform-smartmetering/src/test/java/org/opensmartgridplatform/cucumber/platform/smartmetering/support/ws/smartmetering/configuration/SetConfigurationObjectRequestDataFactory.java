// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData;

public class SetConfigurationObjectRequestDataFactory {

  private SetConfigurationObjectRequestDataFactory() {
    // Private constructor for utility class
  }

  public static SetConfigurationObjectRequestData fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetConfigurationObjectRequestData setConfigurationObjectRequestData =
        new SetConfigurationObjectRequestData();
    setConfigurationObjectRequestData.setConfigurationObject(
        ConfigurationObjectFactory.fromParameterMap(requestParameters));
    return setConfigurationObjectRequestData;
  }
}
