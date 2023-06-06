// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.services;

import java.util.Optional;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.InactiveDeviceException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.exceptions.UnregisteredDeviceException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(value = "transactionManager")
public class DeviceDomainService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDomainService.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private SsldRepository ssldRepository;

  @Autowired private SmartMeterRepository smartMeterRepository;

  public Device saveDevice(final Device device) {
    return this.deviceRepository.save(device);
  }

  public Device searchDevice(@Identification final String deviceIdentification)
      throws FunctionalException {

    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    if (device == null) {
      LOGGER.error("Device with id {} could not be found", deviceIdentification);
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.DOMAIN_CORE,
          new UnknownEntityException(Device.class, deviceIdentification));
    }
    return device;
  }

  public Device searchActiveDevice(
      @Identification final String deviceIdentification, final ComponentType osgpComponent)
      throws FunctionalException {

    final Device device = this.searchDevice(deviceIdentification);

    // For smartmeters, we want to able to communicate no matter what the
    // device life cycle status is.
    final Optional<SmartMeter> smartMeter = this.smartMeterRepository.findById(device.getId());
    if (smartMeter.isPresent()) {
      return device;
    }

    if (!device.isActivated()
        || !device.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE)) {
      throw new FunctionalException(
          FunctionalExceptionType.INACTIVE_DEVICE,
          osgpComponent,
          new InactiveDeviceException(deviceIdentification));
    }

    // Note: since this code is still specific for SSLD / PSLD, this null
    // check is needed.
    final Optional<Ssld> ssld = this.ssldRepository.findById(device.getId());

    if (ssld.isPresent() && !ssld.get().isPublicKeyPresent()) {
      throw new FunctionalException(
          FunctionalExceptionType.UNREGISTERED_DEVICE,
          osgpComponent,
          new UnregisteredDeviceException(deviceIdentification));
    }

    return device;
  }
}
