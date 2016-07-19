/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

class DeviceFirmwareConverter extends
        CustomConverter<com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceFirmware, DeviceFirmware> {

    private DeviceRepository deviceRepository;
    private WritableFirmwareRepository firmwareRepository;

    public DeviceFirmwareConverter(final DeviceRepository deviceRepository,
            final WritableFirmwareRepository firmwareRepository) {
        this.deviceRepository = deviceRepository;
        this.firmwareRepository = firmwareRepository;
    }

    @Override
    public DeviceFirmware convert(
            final com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceFirmware source,
            final Type<? extends DeviceFirmware> destination) {

        final Device device = this.deviceRepository.findByDeviceIdentification(source.getDeviceIdentification());
        final Firmware firmware = this.firmwareRepository.findOne(Long.valueOf(source.getFirmware().getId()));

        final DeviceFirmware output = new DeviceFirmware();

        output.setActive(source.isActive());
        output.setInstallationDate(source.getInstallationDate().toGregorianCalendar().getTime());
        output.setInstalledBy(source.getInstalledBy());
        output.setDevice(device);
        output.setFirmware(firmware);

        return output;
    }
}