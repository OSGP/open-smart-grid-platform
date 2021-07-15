/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_LAST_COMMUNICATION_TIME;

import io.cucumber.java.en.Given;
import java.util.Map;
import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** RTU device specific steps. */
public class RtuDeviceSteps extends BaseDeviceSteps {

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private DomainInfoRepository domainInfoRepository;

  @Given("^an rtu device$")
  @Transactional("txMgrCore")
  public RtuDevice anRtuDevice(final Map<String, String> settings) {

    final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final RtuDevice rtuDevice = new RtuDevice(deviceIdentification);
    rtuDevice.setDomainInfo(
        this.domainInfoRepository.findByDomainAndDomainVersion(
            getString(settings, PlatformKeys.KEY_DOMAIN, PlatformDefaults.DOMAIN),
            getString(settings, PlatformKeys.KEY_DOMAIN_VERSION, PlatformDefaults.DOMAIN_VERSION)));
    rtuDevice.messageReceived(this.getLastCommunicationTime(settings).toDate().toInstant());
    return this.rtuDeviceRepository.save(rtuDevice);
  }

  @Transactional("txMgrCore")
  public Device updateRtuDevice(final Map<String, String> settings) {
    return this.updateDevice(
        this.deviceRepository.findByDeviceIdentification(
            getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION)),
        settings);
  }

  private DateTime getLastCommunicationTime(final Map<String, String> settings) {
    if (settings.containsKey(KEY_LAST_COMMUNICATION_TIME)) {
      return DateTimeHelper.getDateTime(settings.get(KEY_LAST_COMMUNICATION_TIME));
    }
    return DateTime.now();
  }
}
