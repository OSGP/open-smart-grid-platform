/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.entities.Ean;
import com.alliander.osgp.domain.core.entities.Ssld;

class SsldConverter extends
        BidirectionalConverter<Ssld, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> {

    /*
     * (non-Javadoc)
     *
     * @see ma.glasnost.orika.converter.BidirectionalConverter#convertTo(java
     * .lang.Object, ma.glasnost.orika.metadata.Type)
     */
    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final Ssld source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {
        return DeviceConverter.convertToStatic(source, destinationType);
    }

    /*
     * (non-Javadoc)
     *
     * @see ma.glasnost.orika.converter.BidirectionalConverter#convertFrom(java
     * .lang.Object, ma.glasnost.orika.metadata.Type)
     */
    @Override
    public Ssld convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<Ssld> destinationType) {
        Ssld destination = null;

        if (source != null) {

            if (source.getGpsLatitude() == null) {
                source.setGpsLatitude("0");
            }
            if (source.getGpsLongitude() == null) {
                source.setGpsLongitude("0");
            }

            destination = new Ssld(source.getDeviceIdentification(), source.getAlias(), source.getContainerCity(),
                    source.getContainerPostalCode(), source.getContainerStreet(), source.getContainerNumber(),
                    source.getContainerMunicipality(), Float.valueOf(source.getGpsLatitude()), Float.valueOf(source
                            .getGpsLongitude()));

            final List<com.alliander.osgp.domain.core.entities.DeviceOutputSetting> deviceOutputSettings = new ArrayList<com.alliander.osgp.domain.core.entities.DeviceOutputSetting>();

            for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting deviceOutputSetting : source
                    .getOutputSettings()) {
                com.alliander.osgp.domain.core.entities.DeviceOutputSetting newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting();

                newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting(
                        deviceOutputSetting.getInternalId(), deviceOutputSetting.getExternalId(),
                        deviceOutputSetting.getRelayType() == null ? null
                                : com.alliander.osgp.domain.core.valueobjects.RelayType.valueOf(deviceOutputSetting
                                        .getRelayType().name()), deviceOutputSetting.getAlias());

                deviceOutputSettings.add(newDeviceOutputSetting);
            }
            destination.updateOutputSettings(deviceOutputSettings);
            destination.setPublicKeyPresent(source.isPublicKeyPresent());
            destination.setHasSchedule(source.isHasSchedule());
            destination.setActivated(source.isActivated());

            if (source.getTechnicalInstallationDate() != null) {
                destination.setTechnicalInstallationDate(source.getTechnicalInstallationDate().toGregorianCalendar()
                        .getTime());
            }

            // clearing the existing Eans to prevent duplication
            destination.setEans(new ArrayList<Ean>());

            for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean ean : source.getEans()) {
                final Ean newEan = new Ean(destination, ean.getCode(), ean.getDescription());

                destination.getEans().add(newEan);
            }

            return destination;
        }
        return null;
    }

}