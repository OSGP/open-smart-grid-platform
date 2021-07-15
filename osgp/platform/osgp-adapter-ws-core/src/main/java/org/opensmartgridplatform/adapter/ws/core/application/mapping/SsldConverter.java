/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.RelayType;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Ean;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

class SsldConverter extends BidirectionalConverter<Ssld, Device> {

    private final DeviceConverterHelper<Ssld> helper = new DeviceConverterHelper<>(Ssld.class);

    private final SsldRepository ssldRepository;

    public SsldConverter(final SsldRepository ssldRepository) {
        super();
        this.ssldRepository = ssldRepository;
    }

    @Override
    public void setMapperFacade(final MapperFacade mapper) {
        super.setMapperFacade(mapper);
        this.helper.setMapperFacade(mapper);
    }

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device convertTo(final Ssld source,
            final Type<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device> destinationType,
            final MappingContext context) {

        if (source == null) {
            return null;
        }
        final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device destination = this.helper
                .initJaxb(source);

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(source.getDeviceIdentification());

        if (ssld != null) {
            final List<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting> deviceOutputSettings = new ArrayList<>();
            for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
                final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting newDeviceOutputSetting = new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting();

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

            final List<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Ean> eans = new ArrayList<>();
            for (final org.opensmartgridplatform.domain.core.entities.Ean ean : ssld.getEans()) {
                final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Ean newEan = new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Ean();
                newEan.setCode(ean.getCode());
                newEan.setDescription(ean.getDescription());
                eans.add(newEan);
            }
            destination.getEans().addAll(eans);

            this.addRelayStatuses(destination, ssld);
        }

        return destination;
    }

    @Override
    public Ssld convertFrom(final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<Ssld> destinationType, final MappingContext context) {

        if (source == null) {
            return null;
        }

        final Ssld destination = this.helper.initEntity(source);

        final List<org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting> deviceOutputSettings = new ArrayList<>();

        for (final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting deviceOutputSetting : source
                .getOutputSettings()) {
            final org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting newDeviceOutputSetting = new org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting(
                    deviceOutputSetting.getInternalId(), deviceOutputSetting.getExternalId(),
                    deviceOutputSetting.getRelayType() == null ? null
                            : org.opensmartgridplatform.domain.core.valueobjects.RelayType
                                    .valueOf(deviceOutputSetting.getRelayType().name()),
                    deviceOutputSetting.getAlias());

            deviceOutputSettings.add(newDeviceOutputSetting);
        }
        destination.updateOutputSettings(deviceOutputSettings);

        if (source.isPublicKeyPresent() != null) {
            destination.setPublicKeyPresent(source.isPublicKeyPresent());
        }

        if (source.isHasSchedule() != null) {
            destination.setHasSchedule(source.isHasSchedule());
        }
        if (source.isActivated() != null) {
            destination.setActivated(source.isActivated());
        }

        if (source.getDeviceLifecycleStatus() != null) {
            destination
                    .setDeviceLifecycleStatus(DeviceLifecycleStatus.valueOf(source.getDeviceLifecycleStatus().name()));
        }

        // clearing the existing Eans to prevent duplication
        destination.setEans(new ArrayList<Ean>());

        for (final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Ean ean : source.getEans()) {
            final Ean newEan = new Ean(destination, ean.getCode(), ean.getDescription());

            destination.getEans().add(newEan);
        }

        return destination;
    }

    private void addRelayStatuses(
            final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device destination,
            final Ssld ssld) {
        if (ssld.getRelayStatuses() != null) {
            for (final org.opensmartgridplatform.domain.core.entities.RelayStatus r : ssld.getRelayStatuses()) {
                final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.RelayStatus temp = this
                        .convertRelayStatus(r);
                if (temp != null) {
                    destination.getRelayStatuses().add(temp);
                }
            }
        }
    }

    private org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.RelayStatus convertRelayStatus(
            final RelayStatus status) {

        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.RelayStatus output = null;

        if (status != null) {
            output = new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.RelayStatus();
            output.setIndex(status.getIndex());
            output.setLastKnownState(status.isLastKnownState());
            output.setLastSwitchingEventState(status.isLastSwitchingEventState());
            output.setLastSwitchingEventTime(
                    this.mapperFacade.map(status.getLastSwitchingEventTime(), XMLGregorianCalendar.class));
            output.setLastKnownStateTime(
                    this.mapperFacade.map(status.getLastKnownStateTime(), XMLGregorianCalendar.class));
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
