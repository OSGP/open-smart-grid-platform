// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.SystemService;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec61850SystemServiceFactory {

  @Autowired private Iec61850RtuSystemService iec61850RtuSystemService;

  @Autowired private Iec61850PvSystemService iec61850PvSystemService;

  @Autowired private Iec61850BatterySystemService iec61850BatterySystemService;

  @Autowired private Iec61850EngineSystemService iec61850EngineSystemService;

  @Autowired private Iec61850LoadSystemService iec61850LoadSystemService;

  @Autowired private Iec61850HeatBufferSystemService iec61850HeatBufferSystemService;

  @Autowired private Iec61850ChpSystemService iec61850ChpSystemService;

  @Autowired private Iec61850GasFurnaceSystemService iec61850GasFurnaceSystemService;

  @Autowired private Iec61850BoilerSystemService iec61850BoilerSystemService;

  @Autowired private Iec61850HeatPumpSystemService iec61850HeatPumpSystemService;

  @Autowired private Iec61850WindSystemService iec61850WindSystemService;

  @Autowired private Iec61850PqSystemService iec61850PqSystemService;

  private Map<String, SystemService> systemServices;

  public SystemService getSystemService(final SystemFilterDto systemFilter)
      throws ProtocolAdapterException {
    return this.getSystemService(systemFilter.getSystemType());
  }

  public SystemService getSystemService(final String systemType) throws ProtocolAdapterException {
    final String key = systemType.toUpperCase(Locale.ENGLISH);
    if (this.getSystemServices().containsKey(key)) {
      return this.getSystemServices().get(key);
    }

    throw new ProtocolAdapterException("Invalid System Type in System Filter: [" + key + "]");
  }

  private Map<String, SystemService> getSystemServices() {
    if (this.systemServices == null) {
      this.systemServices = new HashMap<>();

      this.systemServices.put(LogicalDevice.RTU.name(), this.iec61850RtuSystemService);
      this.systemServices.put(LogicalDevice.PV.name(), this.iec61850PvSystemService);
      this.systemServices.put(LogicalDevice.BATTERY.name(), this.iec61850BatterySystemService);
      this.systemServices.put(LogicalDevice.ENGINE.name(), this.iec61850EngineSystemService);
      this.systemServices.put(LogicalDevice.LOAD.name(), this.iec61850LoadSystemService);
      this.systemServices.put(
          LogicalDevice.HEAT_BUFFER.name(), this.iec61850HeatBufferSystemService);
      this.systemServices.put(LogicalDevice.CHP.name(), this.iec61850ChpSystemService);
      this.systemServices.put(
          LogicalDevice.GAS_FURNACE.name(), this.iec61850GasFurnaceSystemService);
      this.systemServices.put(LogicalDevice.BOILER.name(), this.iec61850BoilerSystemService);
      this.systemServices.put(LogicalDevice.HEAT_PUMP.name(), this.iec61850HeatPumpSystemService);
      this.systemServices.put(LogicalDevice.WIND.name(), this.iec61850WindSystemService);
      this.systemServices.put(LogicalDevice.PQ.name(), this.iec61850PqSystemService);
    }
    return this.systemServices;
  }
}
