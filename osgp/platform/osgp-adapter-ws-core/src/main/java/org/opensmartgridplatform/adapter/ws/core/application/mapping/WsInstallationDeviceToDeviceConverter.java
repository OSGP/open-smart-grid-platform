/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.List;
import java.util.Objects;

import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WsInstallationDeviceToDeviceConverter
        extends BidirectionalConverter<Device, org.opensmartgridplatform.domain.core.entities.Device> {

    private SsldRepository ssldRepository;

    private WritableDeviceModelRepository writableDeviceModelRepository;

    public WsInstallationDeviceToDeviceConverter(final SsldRepository ssldRepository,
            final WritableDeviceModelRepository writableDeviceModelRepository) {
        this.ssldRepository = ssldRepository;
        this.writableDeviceModelRepository = writableDeviceModelRepository;
    }

    @Override
    public Device convertFrom(final org.opensmartgridplatform.domain.core.entities.Device source,
            final Type<Device> destinationType, final MappingContext context) {

        Device destination = null;
        if (source != null) {
            destination = new Device();
            destination.setDeviceIdentification(source.getDeviceIdentification());
            destination.setAlias(source.getAlias());

            if (!Objects.isNull(source.getContainerAddress())) {
                final Address container = source.getContainerAddress();
                destination.setContainerCity(container.getCity());
                destination.setContainerPostalCode(container.getPostalCode());
                destination.setContainerStreet(container.getStreet());
                destination.setContainerNumber(container.getNumber());
                destination.setContainerMunicipality(container.getMunicipality());
            }

            if (!Objects.isNull(source.getGpsCoordinates())) {
                final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
                destination.setGpsLatitude(gpsCoordinates.getLatitude());
                destination.setGpsLongitude(gpsCoordinates.getLongitude());
            }

            destination.setActivated(source.isActivated());
            destination.setHasSchedule(this.ssldRepository.findOne(source.getId()).getHasSchedule());

            return destination;
        }
        return null;
    }

    @Override
    public org.opensmartgridplatform.domain.core.entities.Device convertTo(final Device source,
            final Type<org.opensmartgridplatform.domain.core.entities.Device> destinationType,
            final MappingContext context) {

        org.opensmartgridplatform.domain.core.entities.Device destination = null;

        if (source != null) {
            destination = new org.opensmartgridplatform.domain.core.entities.Device(source.getDeviceIdentification(),
                    source.getAlias(),
                    new Address(source.getContainerCity(), source.getContainerPostalCode(),
                            source.getContainerStreet(), source.getContainerNumber(),
                            source.getContainerMunicipality()),
                    new GpsCoordinates(source.getGpsLatitude(), source.getGpsLongitude()), null);

            /*
             * Model code does not uniquely identify a device model, which is
             * why deviceModelRepository is changed to return a list of device
             * models.
             *
             * A better solution would be to determine the manufacturer and do a
             * lookup by manufacturer and model code, which should uniquely
             * define the device model.
             */
            final List<DeviceModel> deviceModels = this.writableDeviceModelRepository
                    .findByModelCode(source.getDeviceModel().getModelCode());

            if (deviceModels.size() > 1) {
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
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.ssldRepository, this.writableDeviceModelRepository);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof WsInstallationDeviceToDeviceConverter)) {
            return false;
        }

        final WsInstallationDeviceToDeviceConverter other = (WsInstallationDeviceToDeviceConverter) obj;
        return super.equals(other) && Objects.equals(this.ssldRepository, other.ssldRepository)
                && Objects.equals(this.writableDeviceModelRepository, other.writableDeviceModelRepository);
    }
}
