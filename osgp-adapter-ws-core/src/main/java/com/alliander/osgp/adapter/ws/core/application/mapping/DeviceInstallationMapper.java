package com.alliander.osgp.adapter.ws.core.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;

import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.entities.Device;

@Component(value = "coreDeviceInstallationMapper")
public class DeviceInstallationMapper extends ConfigurableMapper {

    private static class DeviceConverter extends BidirectionalConverter<Device, com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device> {

        @Override
        public Device convertFrom(final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device source, final Type<Device> destinationType) {

            Device destination = null;

            if (source != null) {
                destination = new Device(source.getDeviceIdentification(), source.getContainerCity(), source.getContainerPostalCode(),
                        source.getContainerStreet(), source.getContainerNumber(), source.getGpsLatitude(), source.getGpsLongitude());

                return destination;
            }
            return null;
        }

        @Override
        public com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device convertTo(final Device source,
                final Type<com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device> destinationType) {
            com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device destination = null;
            if (source != null) {
                destination = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
                destination.setDeviceIdentification(source.getDeviceIdentification());
                destination.setContainerCity(source.getContainerCity());
                destination.setContainerPostalCode(source.getContainerPostalCode());
                destination.setContainerStreet(source.getContainerStreet());
                destination.setContainerNumber(source.getContainerNumber());
                destination.setGpsLatitude(source.getGpsLatitude());
                destination.setGpsLongitude(source.getGpsLongitude());

                destination.setActivated(source.isActivated());
                destination.setHasSchedule(source.getHasSchedule());

                return destination;
            }
            return null;
        }
    }

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new DeviceConverter());
    }
}
