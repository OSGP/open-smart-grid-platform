/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.GregorianCalendar;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

class FirmwareConverter extends CustomConverter<org.opensmartgridplatform.domain.core.entities.FirmwareFile, Firmware> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareConverter.class);

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware convert(
            final org.opensmartgridplatform.domain.core.entities.FirmwareFile source,
            final Type<? extends org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware> destinationType,
            final MappingContext context) {

        final Firmware output = new Firmware();

        /*
         * A firmware file has been changed to be related to (possibly) multiple
         * device models to be usable across different value streams for all
         * kinds of devices.
         *
         * If this code gets used in a scenario where multiple device models are
         * actually related to the firmware file it may need to be updated to
         * deal with this.
         */
        final Set<DeviceModel> deviceModels = source.getDeviceModels();
        if (deviceModels.size() != 1) {
            LOGGER.warn("Conversion to WS Firmware assumes a single DeviceModel, FirmwareFile (id={}) has {}: {}",
                    source.getId(), deviceModels.size(), deviceModels);
        }
        final DeviceModel deviceModel = deviceModels.iterator().next();

        output.setDescription(source.getDescription());
        output.setFilename(source.getFilename());
        output.setId(source.getId().intValue());
        output.setModelCode(deviceModel.getModelCode());
        output.setPushToNewDevices(source.getPushToNewDevices());
        output.setManufacturer(deviceModel.getManufacturer().getCode());
        output.setActive(source.isActive());

        final FirmwareModuleData firmwareModuleData = new FirmwareModuleData();
        firmwareModuleData.setModuleVersionComm(source.getModuleVersionComm());
        firmwareModuleData.setModuleVersionFunc(source.getModuleVersionFunc());
        firmwareModuleData.setModuleVersionMa(source.getModuleVersionMa());
        firmwareModuleData.setModuleVersionMbus(source.getModuleVersionMbus());
        firmwareModuleData.setModuleVersionSec(source.getModuleVersionSec());
        output.setFirmwareModuleData(firmwareModuleData);

        final GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(source.getCreationTime());

        try {
            output.setCreationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar));
        } catch (final DatatypeConfigurationException e) {
            // This won't happen, so no further action is needed.
            LOGGER.error("Bad date format in one of Firmware installation dates", e);
        }

        return output;
    }
}
