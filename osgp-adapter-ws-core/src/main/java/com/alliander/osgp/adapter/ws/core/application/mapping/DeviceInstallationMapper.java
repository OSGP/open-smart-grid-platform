/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;

@Component(value = "coreDeviceInstallationMapper")
public class DeviceInstallationMapper extends ConfigurableMapper {

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private WritableDeviceModelRepository writableDeviceModelRepository;

    public DeviceInstallationMapper() {
        super(false);
    }

    @PostConstruct
    public void initialize() {
        this.init();
    }

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory()
                .registerConverter(new DeviceConverter(this.ssldRepository, this.writableDeviceModelRepository));
    }

    private static class DeviceConverter extends
            BidirectionalConverter<Device, com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device> {

        private SsldRepository ssldRepository;

        private WritableDeviceModelRepository writableDeviceModelRepository;

        public DeviceConverter(final SsldRepository ssldRepository,
                final WritableDeviceModelRepository writableDeviceModelRepository) {
            this.ssldRepository = ssldRepository;
            this.writableDeviceModelRepository = writableDeviceModelRepository;
        }

        @Override
        public Device convertFrom(final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device source,
                final Type<Device> destinationType, final MappingContext context) {

            Device destination = null;

            if (source != null) {
                destination = new Device(source.getDeviceIdentification(), source.getAlias(), source.getContainerCity(),
                        source.getContainerPostalCode(), source.getContainerStreet(), source.getContainerNumber(),
                        source.getContainerMunicipality(), source.getGpsLatitude(), source.getGpsLongitude());

                /*
                 * Model code does not uniquely identify a device model, which
                 * is why deviceModelRepository is changed to return a list of
                 * device models.
                 *
                 * A better solution would be to determine the manufacturer and
                 * do a lookup by manufacturer and model code, which should
                 * uniquely define the device model.
                 */
                final List<DeviceModel> deviceModels = this.writableDeviceModelRepository
                        .findByModelCode(source.getDeviceModel().getModelCode());

                if (deviceModels.size() > 1) {
                    // TODO update code to deal with non-unique model code.
                    throw new AssertionError("Model code \"" + source.getDeviceModel().getModelCode()
                            + "\" does not uniquely identify a device model.");
                }
                if (!deviceModels.isEmpty()) {
                    destination.setDeviceModel(deviceModels.get(0));
                }

                return destination;
            }
            return null;
        }

        @Override
        public com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device convertTo(final Device source,
                final Type<com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device> destinationType,
                final MappingContext context) {

            com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device destination = null;
            if (source != null) {
                destination = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
                destination.setDeviceIdentification(source.getDeviceIdentification());
                destination.setAlias(source.getAlias());
                destination.setContainerCity(source.getContainerCity());
                destination.setContainerPostalCode(source.getContainerPostalCode());
                destination.setContainerStreet(source.getContainerStreet());
                destination.setContainerNumber(source.getContainerNumber());
                destination.setContainerMunicipality(source.getContainerMunicipality());
                destination.setGpsLatitude(source.getGpsLatitude());
                destination.setGpsLongitude(source.getGpsLongitude());

                destination.setActivated(source.isActivated());
                destination.setHasSchedule(this.ssldRepository.findOne(source.getId()).getHasSchedule());

                return destination;
            }
            return null;
        }
    }
}
