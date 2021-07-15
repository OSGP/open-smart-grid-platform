/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform;

import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LightMeasurementRtuDeviceCreator extends AbstractPlatformDeviceCreator<RtuDevice> {

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Override
  public RtuDevice apply(final Protocol protocol, final Map<String, String> settings) {
    RtuDevice device = new RtuDevice(this.deviceIdentification(settings));
    device.setDeviceType(DeviceType.LIGHT_MEASUREMENT_RTU.getPlatformDeviceType());
    device.setNetworkAddress(this.networkAddress(settings));
    device.setDeviceLifecycleStatus(this.deviceLifecycleStatus(settings));
    device.setActivated(this.activated(settings));
    device.updateProtocol(this.protocolInfo(protocol));
    device.setDomainInfo(this.domainInfo());

    device = this.rtuDeviceRepository.save(device);

    this.addDeviceAuthorization(device, settings);

    return device;
  }
}
