/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmwareFile;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

class DeviceFirmwareConverter extends
        CustomConverter<com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceFirmware, DeviceFirmwareFile> {

    private DeviceRepository deviceRepository;
    private WritableFirmwareFileRepository firmwareFileRepository;

    public DeviceFirmwareConverter(final DeviceRepository deviceRepository,
            final WritableFirmwareFileRepository firmwareFileRepository) {
        this.deviceRepository = deviceRepository;
        this.firmwareFileRepository = firmwareFileRepository;
    }

    @Override
    public DeviceFirmwareFile convert(
            final com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceFirmware source,
            final Type<? extends DeviceFirmwareFile> destination, final MappingContext context) {

        final Device device = this.deviceRepository.findByDeviceIdentification(source.getDeviceIdentification());
        final FirmwareFile firmwareFile = this.firmwareFileRepository.findOne(Long.valueOf(source.getFirmware().getId()));

        return new DeviceFirmwareFile(device, firmwareFile,
                source.getInstallationDate().toGregorianCalendar().getTime(), source.getInstalledBy());
    }
}