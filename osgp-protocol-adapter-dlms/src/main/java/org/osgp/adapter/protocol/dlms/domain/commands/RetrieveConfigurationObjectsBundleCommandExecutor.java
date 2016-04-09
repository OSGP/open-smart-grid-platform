/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationResponseDataDto;

@Component
public class RetrieveConfigurationObjectsBundleCommandExecutor implements
CommandExecutor<GetConfigurationRequestDataDto, GetConfigurationResponseDataDto> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RetrieveConfigurationObjectsBundleCommandExecutor.class);

    private static final String ERROR_RETRIEVING_CONFIGURATION_OBJECTS_FOR_DEVICE = "Error retrieving configuration objects for device: ";

    @Autowired
    private RetrieveConfigurationObjectsCommandExecutor retrieveConfigurationObjectsCommandExecutor;

    @Override
    public GetConfigurationResponseDataDto execute(final ClientConnection conn, final DlmsDevice device,
            final GetConfigurationRequestDataDto getConfigurationRequestDataDto) {

        final String resultString;
        try {
            resultString = this.retrieveConfigurationObjectsCommandExecutor.execute(conn, device, null);
        } catch (final ProtocolAdapterException e) {

            LOGGER.error(ERROR_RETRIEVING_CONFIGURATION_OBJECTS_FOR_DEVICE + device.getDeviceIdentification(), e);

            final GetConfigurationResponseDataDto getConfigurationResponseDataDto = new GetConfigurationResponseDataDto();
            getConfigurationResponseDataDto.setException(e);
            getConfigurationResponseDataDto.setResultString(ERROR_RETRIEVING_CONFIGURATION_OBJECTS_FOR_DEVICE
                    + device.getDeviceIdentification());
            return getConfigurationResponseDataDto;
        }

        return new GetConfigurationResponseDataDto(resultString);

    }

}
