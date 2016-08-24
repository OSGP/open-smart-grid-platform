/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayType;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Ean;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

class SsldConverter extends BidirectionalConverter<Ssld, Device> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SsldConverter.class);
    private final DeviceConverterHelper<Ssld> helper = new DeviceConverterHelper<>(Ssld.class);

    private final SsldRepository ssldRepository;

    public SsldConverter(final SsldRepository ssldRepository) {
        super();
        this.ssldRepository = ssldRepository;
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final Ssld source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {

        if (source == null) {
            return null;
        }
        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination = this.helper
                .initJaxb(source);

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(source.getDeviceIdentification());

        if (ssld != null) {
            final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting> deviceOutputSettings = new ArrayList<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting>();
            for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
                final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting newDeviceOutputSetting = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting();

                newDeviceOutputSetting.setExternalId(deviceOutputSetting.getExternalId());
                newDeviceOutputSetting.setInternalId(deviceOutputSetting.getInternalId());
                newDeviceOutputSetting.setRelayType(deviceOutputSetting.getOutputType() == null ? null
                        : RelayType.valueOf(deviceOutputSetting.getOutputType().name()));
                newDeviceOutputSetting.setAlias(deviceOutputSetting.getAlias());
                deviceOutputSettings.add(newDeviceOutputSetting);
            }
            destination.getOutputSettings().addAll(deviceOutputSettings);

            destination.setPublicKeyPresent(ssld.isPublicKeyPresent());
            destination.setHasSchedule(ssld.getHasSchedule());

            final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean> eans = new ArrayList<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean>();
            for (final com.alliander.osgp.domain.core.entities.Ean ean : ssld.getEans()) {
                final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean newEan = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean();
                newEan.setCode(ean.getCode());
                newEan.setDescription(ean.getDescription());
                eans.add(newEan);
            }
            destination.getEans().addAll(eans);

            this.addRelayStatusses(destination, ssld);
        }

        return destination;
    }

    @Override
    public Ssld convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<Ssld> destinationType) {

        if (source == null) {
            return null;
        }

        final Ssld destination = this.helper.initEntity(source);

        final List<com.alliander.osgp.domain.core.entities.DeviceOutputSetting> deviceOutputSettings = new ArrayList<com.alliander.osgp.domain.core.entities.DeviceOutputSetting>();

        for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting deviceOutputSetting : source
                .getOutputSettings()) {
            com.alliander.osgp.domain.core.entities.DeviceOutputSetting newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting();

            newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting(
                    deviceOutputSetting.getInternalId(), deviceOutputSetting.getExternalId(),
                    deviceOutputSetting.getRelayType() == null ? null
                            : com.alliander.osgp.domain.core.valueobjects.RelayType
                                    .valueOf(deviceOutputSetting.getRelayType().name()),
                    deviceOutputSetting.getAlias());

            deviceOutputSettings.add(newDeviceOutputSetting);
        }
        destination.updateOutputSettings(deviceOutputSettings);
        destination.setPublicKeyPresent(source.isPublicKeyPresent());
        destination.setHasSchedule(source.isHasSchedule());
        destination.setActivated(source.isActivated());

        if (source.isActive() != null) {
            destination.setActive(source.isActive());
        }

        // clearing the existing Eans to prevent duplication
        destination.setEans(new ArrayList<Ean>());

        for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean ean : source.getEans()) {
            final Ean newEan = new Ean(destination, ean.getCode(), ean.getDescription());

            destination.getEans().add(newEan);
        }

        return destination;
    }

    private void addRelayStatusses(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination,
            final Ssld ssld) {
        if (ssld.getRelayStatusses() != null) {
            com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayStatus temp = null;
            for (final com.alliander.osgp.domain.core.entities.RelayStatus r : ssld.getRelayStatusses()) {
                temp = this.convertRelayStatus(r);

                if (temp != null) {
                    destination.getRelayStatuses().add(temp);
                }
            }
        }
    }

    private com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayStatus convertRelayStatus(
            final RelayStatus status) {

        com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayStatus output = null;

        if (status != null) {

            output = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayStatus();
            output.setIndex(status.getIndex());
            output.setLastKnownState(status.isLastKnownState());

            final GregorianCalendar gCalendar = new GregorianCalendar();
            gCalendar.setTime(status.getLastKnowSwitchingTime());

            try {
                output.setLastKnowSwitchingTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar));
            } catch (final DatatypeConfigurationException e) {
                // This won't happen, so no further action is needed.
                LOGGER.error("Bad date format in one of theRelay Status dates", e);
            }
        }
        return output;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.helper, this.ssldRepository);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && Objects.equals(this.helper, ((SsldConverter) obj).helper)
                && Objects.equals(this.ssldRepository, ((SsldConverter) obj).ssldRepository);
    }

}
