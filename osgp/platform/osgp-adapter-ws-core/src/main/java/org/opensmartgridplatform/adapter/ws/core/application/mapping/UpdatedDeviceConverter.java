package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class UpdatedDeviceConverter extends BidirectionalConverter<Device, UpdatedDevice> {
    final DeviceConverterHelper<UpdatedDevice> deviceConverter = new DeviceConverterHelper<UpdatedDevice.class>();

    @Override
    public UpdatedDevice convertTo(final Device source, final Type<UpdatedDevice> destinationType,
            final MappingContext mappingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Device convertFrom(final UpdatedDevice source, final Type<Device> destinationType,
            final MappingContext mappingContext) {
        // TODO Auto-generated method stub
        return null;
    }

}
