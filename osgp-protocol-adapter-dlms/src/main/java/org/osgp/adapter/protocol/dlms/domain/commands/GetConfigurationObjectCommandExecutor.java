/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;

@Component()
public class GetConfigurationObjectCommandExecutor extends AbstractCommandExecutor<DataObject, ConfigurationObjectDto> {

    @Autowired
    private GetConfigurationObjectHelper getConfigurationObjectHelper;

    public GetConfigurationObjectCommandExecutor() {
        super(GetConfigurationObjectRequestDataDto.class);
    }

    @Override
    public DataObject fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        /*
         * The DataObject input is ignored. (@see
         * GetConfigurationObjectCommandExecutor.execute)
         */
        return null;
    }

    @Override
    public ActionResponseDto asBundleResponse(final ConfigurationObjectDto executionResult)
            throws ProtocolAdapterException {
        return new GetConfigurationObjectResponseDto(executionResult);
    }

    @Override
    public ConfigurationObjectDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final DataObject object) throws ProtocolAdapterException {

        return this.getConfigurationObjectHelper.getConfigurationObjectDto(conn);
    }

}
