// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.services;

import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.exceptions.InactiveDeviceException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(value = "transactionManager")
public class SmartMeterDomainService {

  @Autowired private SmartMeterRepository smartMeterRepository;

  public SmartMeter searchSmartMeter(@Identification final String deviceIdentification)
      throws UnknownEntityException {

    final SmartMeter smartMeter =
        this.smartMeterRepository.findByDeviceIdentification(deviceIdentification);

    if (smartMeter == null) {
      throw new UnknownEntityException(SmartMeter.class, deviceIdentification);
    }

    return smartMeter;
  }

  /**
   * @param deviceIdentification the identification of the active device we're looking for
   * @return the active device for the given identification
   * @throws FunctionalException when the device is not in the database or is not in use
   */
  public SmartMeter searchActiveSmartMeter(
      @Identification final String deviceIdentification, final ComponentType osgpComponent)
      throws FunctionalException {

    final SmartMeter smartMeter =
        this.smartMeterRepository.findByDeviceIdentification(deviceIdentification);

    if (smartMeter == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          osgpComponent,
          new UnknownEntityException(SmartMeter.class, deviceIdentification));
    }

    if (!smartMeter.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE)) {
      throw new FunctionalException(
          FunctionalExceptionType.INACTIVE_DEVICE,
          osgpComponent,
          new InactiveDeviceException(deviceIdentification));
    }

    return smartMeter;
  }
}
