/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.SystemService;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Component
public class Iec61850SystemServiceFactory {

    private Map<String, SystemService> systemServices;

    public SystemService getSystemService(final SystemFilterDto systemFilter) throws OsgpException {
        return this.getSystemService(systemFilter.getSystemType());
    }

    public SystemService getSystemService(final String systemType) throws OsgpException {
        final String key = systemType.toUpperCase(Locale.ENGLISH);
        if (this.getSystemServices().containsKey(key)) {
            return this.getSystemServices().get(key);
        }

        throw new OsgpException(ComponentType.PROTOCOL_IEC61850, "Invalid System Type in System Filter: [" + key + "]");
    }

    private Map<String, SystemService> getSystemServices() {
        if (this.systemServices == null) {
            this.systemServices = new HashMap<>();

            this.systemServices.put(LogicalDevice.RTU.name(), new Iec61850RtuSystemService());
            this.systemServices.put(LogicalDevice.PV.name(), new Iec61850PvSystemService());
            this.systemServices.put(LogicalDevice.BATTERY.name(), new Iec61850BatterySystemService());
            this.systemServices.put(LogicalDevice.ENGINE.name(), new Iec61850EngineSystemService());
            this.systemServices.put(LogicalDevice.LOAD.name(), new Iec61850LoadSystemService());
            this.systemServices.put(LogicalDevice.HEAT_BUFFER.name(), new Iec61850HeatBufferSystemService());
            this.systemServices.put(LogicalDevice.CHP.name(), new Iec61850ChpSystemService());
            this.systemServices.put(LogicalDevice.GAS_FURNACE.name(), new Iec61850GasFurnaceSystemService());
        }
        return this.systemServices;
    }
}
