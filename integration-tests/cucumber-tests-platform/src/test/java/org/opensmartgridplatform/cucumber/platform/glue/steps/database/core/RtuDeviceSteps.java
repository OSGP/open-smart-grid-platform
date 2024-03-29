// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_LAST_COMMUNICATION_TIME;

import io.cucumber.java.en.Given;
import java.time.ZonedDateTime;
import java.util.Map;
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
    rtuDevice.messageReceived(this.getLastCommunicationTime(settings).toInstant());
    return this.rtuDeviceRepository.save(rtuDevice);
  }

  @Transactional("txMgrCore")
  public Device updateRtuDevice(final Map<String, String> settings) {
    return this.updateDevice(
        this.deviceRepository.findByDeviceIdentification(
            getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION)),
        settings);
  }

  private ZonedDateTime getLastCommunicationTime(final Map<String, String> settings) {
    if (settings.containsKey(KEY_LAST_COMMUNICATION_TIME)) {
      return DateTimeHelper.getDateTime(settings.get(KEY_LAST_COMMUNICATION_TIME));
    }
    return ZonedDateTime.now();
  }
}
