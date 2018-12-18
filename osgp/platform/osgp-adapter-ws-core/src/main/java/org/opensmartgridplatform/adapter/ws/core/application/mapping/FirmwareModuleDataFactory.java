/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;

public class FirmwareModuleDataFactory implements ObjectFactory<FirmwareModuleData> {
    @Override
    public FirmwareModuleData create(Object source, MappingContext mappingContext) {
        if (source instanceof org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData) {
            return create((org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData) source);
        }
        throw new UnsupportedOperationException("Not implemented yet"); // TODO (RvM): implement.
    }

    private FirmwareModuleData create(
            org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData source) {
        return new FirmwareModuleData(source.getModuleVersionComm(), source.getModuleVersionFunc(),
                source.getModuleVersionMa(), source.getModuleVersionMbus(), source.getModuleVersionSec(),
                source.getModuleVersionMBusDriverActive());
    }

}
