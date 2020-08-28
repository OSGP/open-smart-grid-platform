package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.BaseDeviceModel;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class DeviceModelConverter extends BidirectionalConverter<BaseDeviceModel, DeviceModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceModelConverter.class);

    @Override
    public DeviceModel convertTo(final BaseDeviceModel source, final Type<DeviceModel> destinationType,
            final MappingContext mappingContext) {
        final Manufacturer manufacturer = this.mapperFacade.map(source.getManufacturer(), Manufacturer.class);
        LOGGER.info("Modelcode of base device model source: {}", source.getModelCode());
        if (source instanceof org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel) {
            final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel deviceModel = (org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel) source;
            return new DeviceModel(manufacturer, deviceModel.getModelCode(), deviceModel.getDescription(),
                    deviceModel.isMetered());
        } else {
            return new DeviceModel(manufacturer, source.getModelCode());
        }
    }

    @Override
    public BaseDeviceModel convertFrom(final DeviceModel source, final Type<BaseDeviceModel> destinationType,
            final MappingContext mappingContext) {
        final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Manufacturer manufacturer = this.mapperFacade
                .map(source.getManufacturer(),
                        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Manufacturer.class);
        final BaseDeviceModel destination = new BaseDeviceModel();
        LOGGER.info("Modelcode of device model source: {}", source.getModelCode());
        destination.setManufacturer(manufacturer);
        destination.setModelCode(source.getModelCode());
        if (destinationType.getClass()
                .equals(org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel.class)) {
            final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel deviceModel = (org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel) destination;
            deviceModel.setDescription(source.getDescription());
            deviceModel.setMetered(source.isMetered());
            return deviceModel;
        }
        return destination;
    }

}
