// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ConfigurationObjectFactory;
import org.springframework.util.CollectionUtils;

public class SetConfigurationObjectRequestBuilder {

  private static final GprsOperationModeType DEFAULT_GPRS_OPERATION_MODE_TYPE =
      GprsOperationModeType.ALWAYS_ON;
  private static final ConfigurationFlagType DEFAULT_CONFIGURATION_FLAG_TYPE =
      ConfigurationFlagType.DISCOVER_ON_POWER_ON;
  private static final boolean DEFAULT_CONFIGURATION_FLAG_ENABLED = true;

  private GprsOperationModeType gprsOperationModeType;
  private List<ConfigurationFlag> configurationFlags = new ArrayList<>();

  public SetConfigurationObjectRequestBuilder withDefaults() {
    this.gprsOperationModeType = DEFAULT_GPRS_OPERATION_MODE_TYPE;
    this.configurationFlags = new ArrayList<>();
    this.configurationFlags.add(this.getDefaultConfigurationFlag());
    return this;
  }

  public SetConfigurationObjectRequestBuilder fromParameterMap(
      final Map<String, String> parameters) {
    final ConfigurationObject configurationObject =
        ConfigurationObjectFactory.fromParameterMap(parameters);
    this.gprsOperationModeType = configurationObject.getGprsOperationMode();
    if (configurationObject.getConfigurationFlags() != null) {
      this.configurationFlags = configurationObject.getConfigurationFlags().getConfigurationFlag();
    }
    return this;
  }

  public SetConfigurationObjectRequest build() {
    final ConfigurationObject configurationObject = new ConfigurationObject();
    configurationObject.setGprsOperationMode(this.gprsOperationModeType);
    if (!CollectionUtils.isEmpty(this.configurationFlags)) {
      final ConfigurationFlags configurationFlagsElement = new ConfigurationFlags();
      configurationFlagsElement.getConfigurationFlag().addAll(this.configurationFlags);
      configurationObject.setConfigurationFlags(configurationFlagsElement);
    }
    final SetConfigurationObjectRequest request = new SetConfigurationObjectRequest();
    request.setConfigurationObject(configurationObject);
    return request;
  }

  private ConfigurationFlag getDefaultConfigurationFlag() {
    final ConfigurationFlag configurationFlag = new ConfigurationFlag();
    configurationFlag.setConfigurationFlagType(DEFAULT_CONFIGURATION_FLAG_TYPE);
    configurationFlag.setEnabled(DEFAULT_CONFIGURATION_FLAG_ENABLED);
    return configurationFlag;
  }
}
