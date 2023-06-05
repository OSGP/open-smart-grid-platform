// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_ALIAS;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CHANNEL;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_CITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_MUNICIPALITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_NUMBER;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_NUMBER_ADDITION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_POSTALCODE;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_STREET;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_LATITUDE;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_LONGITUDE;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_SUPPLIER;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_ALIAS;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CHANNEL;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_CITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_MUNICIPALITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_NUMBER;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_NUMBER_ADDITION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_POSTALCODE;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_STREET;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_GATEWAY_DEVICE_ID;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_GPS_LATITUDE;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_GPS_LONGITUDE;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_SUPPLIER;

import io.cucumber.java.en.Given;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class SmartMeterSteps extends BaseDeviceSteps {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private SmartMeterRepository smartMeterRepository;

  /** Given a smart meter exists. */
  @Given("^a smart meter$")
  @Transactional("txMgrCore")
  public Device aSmartMeter(final Map<String, String> settings) {

    final String deviceIdentification =
        getString(settings, KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
    final SmartMeter smartMeter =
        new SmartMeter(
            deviceIdentification,
            getString(settings, KEY_ALIAS, DEFAULT_ALIAS),
            new Address(
                getString(settings, KEY_CONTAINER_CITY, DEFAULT_CONTAINER_CITY),
                getString(settings, KEY_CONTAINER_POSTALCODE, DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, KEY_CONTAINER_STREET, DEFAULT_CONTAINER_STREET),
                getInteger(settings, KEY_CONTAINER_NUMBER, DEFAULT_CONTAINER_NUMBER),
                getString(
                    settings, KEY_CONTAINER_NUMBER_ADDITION, DEFAULT_CONTAINER_NUMBER_ADDITION),
                getString(settings, KEY_CONTAINER_MUNICIPALITY, DEFAULT_CONTAINER_MUNICIPALITY)),
            new GpsCoordinates(
                getFloat(settings, KEY_GPS_LATITUDE, DEFAULT_LATITUDE),
                getFloat(settings, KEY_GPS_LONGITUDE, DEFAULT_LONGITUDE)));

    smartMeter.setSupplier(getString(settings, KEY_SUPPLIER, DEFAULT_SUPPLIER));

    if (settings.containsKey(KEY_GATEWAY_DEVICE_ID)) {
      smartMeter.setChannel(getShort(settings, KEY_CHANNEL, DEFAULT_CHANNEL));
      final Device smartEMeter =
          this.deviceRepository.findByDeviceIdentification(settings.get(KEY_GATEWAY_DEVICE_ID));
      smartMeter.updateGatewayDevice(smartEMeter);
    }

    this.smartMeterRepository.save(smartMeter);

    return this.updateDevice(deviceIdentification, settings);
  }
}
