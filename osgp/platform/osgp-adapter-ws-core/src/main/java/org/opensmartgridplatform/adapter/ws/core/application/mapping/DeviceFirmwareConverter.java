/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.DeviceFirmware;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

class DeviceFirmwareConverter extends
        BidirectionalConverter<org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.DeviceFirmware, DeviceFirmwareFile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceFirmwareConverter.class);

    private final DeviceRepository deviceRepository;
    private final WritableFirmwareFileRepository firmwareFileRepository;

    public DeviceFirmwareConverter(final DeviceRepository deviceRepository,
            final WritableFirmwareFileRepository firmwareFileRepository) {
        this.deviceRepository = deviceRepository;
        this.firmwareFileRepository = firmwareFileRepository;
    }

    @Override
    public DeviceFirmwareFile convertTo(final DeviceFirmware source, final Type<DeviceFirmwareFile> destinationType,
            final MappingContext mappingContext) {
        final Device device = this.deviceRepository.findByDeviceIdentification(source.getDeviceIdentification());
        final FirmwareFile firmwareFile = this.firmwareFileRepository
                .findById(Long.valueOf(source.getFirmware().getId()))
                .orElse(null);

        return new DeviceFirmwareFile(device, firmwareFile,
                source.getInstallationDate().toGregorianCalendar().getTime(), source.getInstalledBy());
    }

    @Override
    public DeviceFirmware convertFrom(final DeviceFirmwareFile source, final Type<DeviceFirmware> destinationType,
            final MappingContext mappingContext) {
        final DeviceFirmware destination = new DeviceFirmware();
        destination.setDeviceIdentification(source.getDevice().getDeviceIdentification());

        final Firmware firmware = this.mapperFacade.map(source.getFirmwareFile(), Firmware.class, mappingContext);
        destination.setFirmware(firmware);

        final GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(source.getInstallationDate());

        try {
            destination.setInstallationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar));
        } catch (final DatatypeConfigurationException e) {
            // This won't happen, so no further action is needed.
            LOGGER.error("Bad date format in the installation date", e);
        }

        destination.setInstalledBy(source.getInstalledBy());
        destination.setActive(true);
        return destination;
    }
}
