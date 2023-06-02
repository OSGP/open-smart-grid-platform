//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.adapterprotocolie61850;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.RtuDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.microgrids.PlatformMicrogridsDefaults;
import org.opensmartgridplatform.cucumber.platform.microgrids.PlatformMicrogridsKeys;
import org.opensmartgridplatform.cucumber.platform.microgrids.config.Iec61850MockServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** IEC61850 specific device steps. */
public class Iec61850DeviceSteps {

  private static final String DEFAULT_DEVICE_TYPE = "RTU";
  private static final String DEFAULT_PROTOCOL = "IEC61850";
  private static final String DEFAULT_PROTOCOL_VERSION = "1.0";
  private static final String DOMAIN = "MICROGRIDS";

  private static final Map<String, String> RTU_DEFAULT_SETTINGS =
      Collections.unmodifiableMap(
          new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
              this.put(PlatformMicrogridsKeys.KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
              this.put(PlatformMicrogridsKeys.KEY_PROTOCOL, DEFAULT_PROTOCOL);
              this.put(PlatformMicrogridsKeys.KEY_PROTOCOL_VERSION, DEFAULT_PROTOCOL_VERSION);
            }
          });

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

  @Autowired private Iec61850MockServerConfig iec61850MockServerConfig;

  @Autowired private RtuDeviceSteps rtuDeviceSteps;

  /** Creates an IEC61850 device. */
  @Given("^an rtu iec61850 device$")
  public void anRtuIec61850Device(final Map<String, String> settings) {

    ScenarioContext.current()
        .put(
            PlatformMicrogridsKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformMicrogridsDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final Map<String, String> rtuSettings =
        SettingsHelper.addAsDefaults(settings, RTU_DEFAULT_SETTINGS);
    rtuSettings.put(
        PlatformMicrogridsKeys.KEY_NETWORKADDRESS,
        this.iec61850MockServerConfig.iec61850MockNetworkAddress());
    rtuSettings.put(PlatformKeys.KEY_DOMAIN, DOMAIN);

    this.rtuDeviceSteps.anRtuDevice(rtuSettings);

    this.rtuDeviceSteps.updateRtuDevice(rtuSettings);

    this.createIec61850Device(rtuSettings);
  }

  @Transactional("txMgrIec61850")
  private void createIec61850Device(final Map<String, String> settings) {

    /*
     * Make sure an ICD filename, port and servername corresponding to the
     * mock server settings will be used from the application to connect to
     * the device. ICD filename, port and servername may be null
     */
    final Iec61850Device iec61850Device =
        new Iec61850Device(
            getString(
                settings,
                PlatformMicrogridsKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformMicrogridsDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    iec61850Device.setIcdFilename(
        getString(settings, PlatformMicrogridsKeys.KEY_IEC61850_ICD_FILENAME));
    iec61850Device.setPort(getInteger(settings, PlatformMicrogridsKeys.KEY_IEC61850_PORT));
    iec61850Device.setServerName(
        getString(settings, PlatformMicrogridsKeys.KEY_IEC61850_SERVERNAME));
    iec61850Device.setEnableAllReportsOnConnect(
        getBoolean(
            settings,
            PlatformMicrogridsKeys.ENABLE_ALL_REPORTS,
            PlatformMicrogridsDefaults.ENABLE_ALL_REPORTS));
    iec61850Device.setUseCombinedLoad(
        getBoolean(
            settings,
            PlatformMicrogridsKeys.USE_COMBINED_LOAD,
            PlatformMicrogridsDefaults.USE_COMBINED_LOAD));

    this.iec61850DeviceRepository.save(iec61850Device);
  }
}
