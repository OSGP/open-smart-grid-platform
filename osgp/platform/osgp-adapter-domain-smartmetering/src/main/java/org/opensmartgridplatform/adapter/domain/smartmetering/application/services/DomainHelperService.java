// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.SmartMeterDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "domainSmartMeteringHelperService")
public class DomainHelperService {

  private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_SMART_METERING;

  @Autowired private SmartMeterDomainService smartMeteringDeviceDomainService;

  /**
   * @param deviceIdentification
   * @return
   * @throws FunctionalException when there is no device
   */
  public SmartMeter findSmartMeter(final String deviceIdentification) throws FunctionalException {
    final SmartMeter smartMeter;
    try {
      smartMeter = this.smartMeteringDeviceDomainService.searchSmartMeter(deviceIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
    }
    return smartMeter;
  }

  /**
   * @param deviceIdentification the identification of the active device we're looking for
   * @return the active device for the given identification
   * @throws FunctionalException the device is either not in the database or not active
   */
  public SmartMeter findActiveSmartMeter(final String deviceIdentification)
      throws FunctionalException {
    return this.smartMeteringDeviceDomainService.searchActiveSmartMeter(
        deviceIdentification, COMPONENT_TYPE);
  }

  public void ensureFunctionalExceptionForUnknownDevice(final String deviceIdentification)
      throws FunctionalException {

    /*
     * findSmartMeteringDevice throws a FunctionalException containing
     * information about the device identification for which no smart
     * metering device could be found.
     */
    this.findSmartMeter(deviceIdentification);
  }

  /**
   * Search the MBus Devices coupled on this SmartMeter.
   *
   * @param smartMeter
   * @return the active device for the given identification
   * @throws FunctionalException when the device is not in the database or is not in use
   */
  public List<SmartMeter> searchMBusDevicesFor(final SmartMeter smartMeter) {

    return this.smartMeteringDeviceDomainService.searchMBusDevicesFor(smartMeter);
  }
}
