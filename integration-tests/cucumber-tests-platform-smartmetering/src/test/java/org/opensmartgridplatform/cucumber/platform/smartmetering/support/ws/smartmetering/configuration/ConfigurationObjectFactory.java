// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import io.micrometer.common.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.springframework.util.CollectionUtils;

public class ConfigurationObjectFactory {

  private ConfigurationObjectFactory() {
    // Private constructor for utility class
  }

  public static ConfigurationObject forGprsOperationModeAndFlags(
      final GprsOperationModeType gprsOperationMode,
      final Set<ConfigurationFlagType> enableFlags,
      final Set<ConfigurationFlagType> disableFlags) {

    final ConfigurationObject configurationObject = new ConfigurationObject();
    configurationObject.setGprsOperationMode(gprsOperationMode);
    final List<ConfigurationFlag> configurationFlagList = new ArrayList<>();
    configurationFlagList.addAll(getFlags(enableFlags, true));
    configurationFlagList.addAll(getFlags(disableFlags, false));
    if (!configurationFlagList.isEmpty()) {
      final ConfigurationFlags configurationFlags = new ConfigurationFlags();
      configurationFlags.getConfigurationFlag().addAll(configurationFlagList);
      configurationObject.setConfigurationFlags(configurationFlags);
    }
    return configurationObject;
  }

  private static List<ConfigurationFlag> getFlags(
      final Set<ConfigurationFlagType> flagTypes, final boolean enabled) {
    if (CollectionUtils.isEmpty(flagTypes)) {
      return Collections.emptyList();
    }
    final List<ConfigurationFlag> configurationFlags = new ArrayList<>();
    for (final ConfigurationFlagType flagType : flagTypes) {
      final ConfigurationFlag configurationFlag = new ConfigurationFlag();
      configurationFlag.setConfigurationFlagType(flagType);
      configurationFlag.setEnabled(enabled);
      configurationFlags.add(configurationFlag);
    }
    return configurationFlags;
  }

  public static ConfigurationObject fromParameterMap(final Map<String, String> requestParameters) {
    final ConfigurationObject configurationObject = new ConfigurationObject();
    setConfigurationFlags(configurationObject, requestParameters);
    configurationObject.setGprsOperationMode(
        ReadSettingsHelper.getEnum(
            requestParameters,
            PlatformSmartmeteringKeys.GPRS_OPERATION_MODE_TYPE,
            GprsOperationModeType.class));
    return configurationObject;
  }

  private static boolean hasConfigurationFlags(final Map<String, String> requestParameters) {
    return requestParameters.containsKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_COUNT);
  }

  private static void setConfigurationFlags(
      final ConfigurationObject configurationObject, final Map<String, String> requestParameters) {

    if (!hasConfigurationFlags(requestParameters)) {
      return;
    }

    final ConfigurationFlags configurationFlags = new ConfigurationFlags();
    final List<ConfigurationFlag> configurationFlagList = new ArrayList<>();
    final int numberOfFlags =
        Integer.parseInt(requestParameters.get(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_COUNT));
    for (int i = 1; i <= numberOfFlags; i++) {
      if (isConfigurationFlagDefined(requestParameters, i)) {
        configurationFlagList.add(getConfigurationFlag(requestParameters, i));
      }
    }
    if (configurationFlagList.isEmpty()) {
      return;
    }
    configurationFlags.getConfigurationFlag().addAll(configurationFlagList);
    configurationObject.setConfigurationFlags(configurationFlags);
  }

  private static ConfigurationFlag getConfigurationFlag(
      final Map<String, String> parameters, final int index) {
    final Boolean configurationFlagEnabled = getConfigurationFlagEnabled(parameters, index);
    final ConfigurationFlag configurationFlag = new ConfigurationFlag();
    configurationFlag.setConfigurationFlagType(getConfigurationFlagType(parameters, index));
    configurationFlag.setEnabled(configurationFlagEnabled);
    return configurationFlag;
  }

  private static ConfigurationFlagType getConfigurationFlagType(
      final Map<String, String> parameters, final int index) {

    final String key =
        SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_TYPE, index);
    return ReadSettingsHelper.getEnum(parameters, key, ConfigurationFlagType.class);
  }

  private static boolean getConfigurationFlagEnabled(
      final Map<String, String> parameters, final int index) {
    final String key =
        SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_ENABLED, index);
    return ReadSettingsHelper.getBoolean(parameters, key);
  }

  private static boolean isConfigurationFlagDefined(
      final Map<String, String> parameters, final int index) {
    final String key =
        SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_ENABLED, index);
    return StringUtils.isNotBlank(parameters.get(key));
  }
}
