//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager")
public class TransactionalDeviceService {

  @Autowired private DeviceDomainService deviceDomainService;

  public void updateDeviceLifecycleStatus(
      final String deviceIdentification, final DeviceLifecycleStatus deviceLifecycleStatus)
      throws FunctionalException {
    final Device device = this.deviceDomainService.searchDevice(deviceIdentification);
    device.setDeviceLifecycleStatus(deviceLifecycleStatus);
    this.deviceDomainService.saveDevice(device);
  }

  public void updateDeviceCdmaSettings(
      final String deviceIdentification, final CdmaSettings cdmaSettings)
      throws FunctionalException {
    final Device device = this.deviceDomainService.searchDevice(deviceIdentification);
    device.updateCdmaSettings(cdmaSettings);
    this.deviceDomainService.saveDevice(device);
  }
}
