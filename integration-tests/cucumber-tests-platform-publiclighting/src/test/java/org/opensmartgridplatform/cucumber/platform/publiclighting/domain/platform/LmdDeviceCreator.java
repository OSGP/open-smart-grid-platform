// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform;

import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.repositories.LightMeasurementDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LmdDeviceCreator extends AbstractPlatformDeviceCreator<LightMeasurementDevice> {

  @Autowired private LightMeasurementDeviceRepository lmdRepository;

  @Override
  public LightMeasurementDevice apply(final Protocol protocol, final Map<String, String> settings) {
    LightMeasurementDevice device = new LightMeasurementDevice(this.deviceIdentification(settings));
    device.setDeviceType(DeviceType.LIGHT_MEASUREMENT_DEVICE.getPlatformDeviceType());
    device.updateGatewayDevice(this.gatewayDevice(settings));
    device.setDeviceLifecycleStatus(this.deviceLifecycleStatus(settings));
    device.setActivated(this.activated(settings));
    device.updateProtocol(this.protocolInfo(protocol));

    device = this.lmdRepository.save(device);

    this.addDeviceAuthorization(device, settings);

    return device;
  }
}
