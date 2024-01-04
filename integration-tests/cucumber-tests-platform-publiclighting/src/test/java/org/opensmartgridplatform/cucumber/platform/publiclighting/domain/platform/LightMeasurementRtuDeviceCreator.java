// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
    device.setNetworkAddress(this.networkAddress(settings).getHostAddress());
    device.setDeviceLifecycleStatus(this.deviceLifecycleStatus(settings));
    device.setActivated(this.activated(settings));
    device.updateProtocol(this.protocolInfo(protocol));
    device.setDomainInfo(this.domainInfo());

    device = this.rtuDeviceRepository.save(device);

    this.addDeviceAuthorization(device, settings);

    return device;
  }
}
