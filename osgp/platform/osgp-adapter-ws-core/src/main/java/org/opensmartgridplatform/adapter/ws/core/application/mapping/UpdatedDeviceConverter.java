/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.BaseDeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Ssld;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class UpdatedDeviceConverter extends BidirectionalConverter<UpdatedDevice, Ssld> {

    @Override
    public Ssld convertTo(final UpdatedDevice source, final Type<Ssld> destinationType,
            final MappingContext mappingContext) {
        final Device device = this.mapperFacade.map(source, Device.class);
        final Ssld ssld = this.mapperFacade.map(device, Ssld.class);
        ssld.setDeviceModel(this.mapperFacade.map(source.getDeviceModel(), DeviceModel.class));
        return ssld;
    }

    @Override
    public UpdatedDevice convertFrom(final Ssld source, final Type<UpdatedDevice> destinationType,
            final MappingContext mappingContext) {
        final Device device = this.mapperFacade.map(source, Device.class);
        final UpdatedDevice updatedDevice = this.mapperFacade.map(device, UpdatedDevice.class);
        final BaseDeviceModel deviceModel = device.getDeviceModel();
        updatedDevice.setDeviceModel(deviceModel);
        return updatedDevice;
    }

}
