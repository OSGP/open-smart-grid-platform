/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Firmware;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;

class FirmwareConverter extends CustomConverter<com.alliander.osgp.domain.core.entities.Firmware, Firmware> {

    static final Logger LOGGER = LoggerFactory.getLogger(FirmwareConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Firmware convert(
            final com.alliander.osgp.domain.core.entities.Firmware source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Firmware> destinationType) {

        final Firmware output = new Firmware();

        output.setDescription(source.getDescription());
        output.setFilename(source.getFilename());
        output.setId(source.getId().intValue());
        output.setModelCode(source.getDeviceModel().getModelCode());
        output.setPushToNewDevices(source.getPushToNewDevices());
        output.setManufacturer(source.getDeviceModel().getManufacturerId().getManufacturerId());

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
            LOGGER.error("Bad date format in one of Firmare installation dates", e);
        }

        return output;
    }
}