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
        // Just to be sure
        ssld.setDeviceModel(this.mapperFacade.map(source.getDeviceModel(), DeviceModel.class));
        return ssld;
    }

    @Override
    public UpdatedDevice convertFrom(final Ssld source, final Type<UpdatedDevice> destinationType,
            final MappingContext mappingContext) {
        final Device device = this.mapperFacade.map(source, Device.class);
        final UpdatedDevice updatedDevice = this.mapperFacade.map(device, UpdatedDevice.class);
        // Just to be sure;
        final BaseDeviceModel deviceModel = device.getDeviceModel();
        updatedDevice.setDeviceModel(deviceModel);
        return updatedDevice;
    }

}
