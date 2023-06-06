// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.core.builders.CucumberBuilder;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class DeviceBuilder extends BaseDeviceBuilder<DeviceBuilder>
    implements CucumberBuilder<Device> {

  private final DeviceRepository deviceRepository;

  public DeviceBuilder(final DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  @Override
  public DeviceBuilder withSettings(final Map<String, String> inputSettings) {
    super.withSettings(inputSettings);
    return this;
  }

  @Override
  public Device build() {
    final Device device =
        new Device(
            this.deviceIdentification,
            this.alias,
            new Address(
                this.containerCity,
                this.containerPostalCode,
                this.containerStreet,
                this.containerNumber,
                this.containerNumberAddition,
                this.containerMunicipality),
            new GpsCoordinates(this.gpsLatitude, this.gpsLongitude),
            null);

    device.updateProtocol(this.protocolInfo);
    device.updateInMaintenance(this.inMaintenance);
    if (this.gatewayDeviceIdentification != null) {
      device.updateGatewayDevice(
          this.deviceRepository.findByDeviceIdentification(this.gatewayDeviceIdentification));
    }
    device.setVersion(this.version);
    device.setDeviceModel(this.deviceModel);
    device.setTechnicalInstallationDate(this.technicalInstallationDate);
    // updateRegistrationData sets the status to IN_USE, so setting of any
    // other status has to be done after that.
    device.updateRegistrationData(this.networkAddress, this.deviceType);
    device.setDeviceLifecycleStatus(this.deviceLifeCycleStatus);

    return device;
  }
}
