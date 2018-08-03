/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;

@Component()
public class GetConfigurationObjectCommandExecutor extends AbstractCommandExecutor<Void, ConfigurationObjectDto> {

    @Autowired
    private GetConfigurationObjectHelper getConfigurationObjectHelper;

    public GetConfigurationObjectCommandExecutor() {
        super(GetConfigurationObjectRequestDataDto.class);
    }

    @Override
    public Void fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        this.checkActionRequestType(bundleInput);
        return null;
    }

    @Override
    public ActionResponseDto asBundleResponse(final ConfigurationObjectDto executionResult)
            throws ProtocolAdapterException {
        return new GetConfigurationObjectResponseDto(executionResult);
    }

    @Override
    public ConfigurationObjectDto execute(final DlmsConnectionHolder conn, final DlmsDevice device, final Void object)
            throws ProtocolAdapterException {

        return this.getConfigurationObjectHelper.getConfigurationObjectDto(conn);
    }

}
