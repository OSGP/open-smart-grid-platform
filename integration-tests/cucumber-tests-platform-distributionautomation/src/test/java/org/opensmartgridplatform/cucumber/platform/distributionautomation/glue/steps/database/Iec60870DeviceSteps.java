// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database;

import io.cucumber.java.en.Given;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.RtuDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.protocol.iec60870.config.Iec60870MockServerConfig;
import org.opensmartgridplatform.cucumber.protocol.iec60870.database.Iec60870Database;
import org.springframework.beans.factory.annotation.Autowired;

/** IEC 60870 device specific steps. */
public class Iec60870DeviceSteps {

  private static final String DEFAULT_DEVICE_TYPE = "RTU";
  private static final String DEFAULT_PROTOCOL = "60870-5-104";
  private static final String DEFAULT_PROTOCOL_VERSION = "1.0";

  private static final Map<String, String> RTU_60870_DEFAULT_SETTINGS;

  static {
    final Map<String, String> settingsMap = new HashMap<>();
    settingsMap.put(PlatformKeys.KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
    settingsMap.put(PlatformKeys.KEY_PROTOCOL, DEFAULT_PROTOCOL);
    settingsMap.put(PlatformKeys.KEY_PROTOCOL_VERSION, DEFAULT_PROTOCOL_VERSION);
    settingsMap.put(
        PlatformDistributionAutomationKeys.PROFILE, PlatformDistributionAutomationDefaults.PROFILE);

    RTU_60870_DEFAULT_SETTINGS = Collections.unmodifiableMap(settingsMap);
  }

  @Autowired private Iec60870Database iec60870Database;

  @Autowired private RtuDeviceSteps rtuDeviceSteps;

  @Autowired private Iec60870MockServerConfig mockServerConfig;

  /** Creates an IEC 60870 RTU. */
  @Given("^an IEC 60870 RTU$")
  public void anIec60870Rtu(final Map<String, String> settings) {

    ScenarioContext.current()
        .put(
            PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final Map<String, String> rtuSettings =
        SettingsHelper.addAsDefaults(settings, RTU_60870_DEFAULT_SETTINGS);

    rtuSettings.put(
        PlatformKeys.KEY_NETWORKADDRESS, this.mockServerConfig.iec60870MockNetworkAddress());

    this.rtuDeviceSteps.anRtuDevice(rtuSettings);

    this.rtuDeviceSteps.updateRtuDevice(rtuSettings);

    this.createIec60870Device(rtuSettings);
  }

  private void createIec60870Device(final Map<String, String> settings) {
    this.iec60870Database.addIec60870Device(DeviceType.DISTRIBUTION_AUTOMATION_DEVICE, settings);
  }
}
