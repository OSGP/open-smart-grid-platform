/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetRandomisationSettingsCommandExecutor
        extends AbstractCommandExecutor<SetRandomisationSettingsRequestDataDto, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetRandomisationSettingsCommandExecutor.class);

    private ObisCode obisCodeDirectAttach = new ObisCode("0.1.94.31.3.255");
    private ObisCode obisCodeRandomisationObject = new ObisCode("0.1.94.31.12.255");

    @Autowired
    private ConfigurationService configurationService;

    public SetRandomisationSettingsCommandExecutor() {
        super(SetRandomisationSettingsRequestDataDto.class);
    }

    @Override
    public SetRandomisationSettingsRequestDataDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {
        this.checkActionRequestType(bundleInput);
        return (SetRandomisationSettingsRequestDataDto) bundleInput;

    }

    @Override
    public ActionResponseDto asBundleResponse(final AccessResultCode executionResult) throws ProtocolAdapterException {
        this.checkAccessResultCode(executionResult);
        return new ActionResponseDto("Set Randomization Settings was successful");
    }

    @Override
    public AccessResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final SetRandomisationSettingsRequestDataDto setRandomisationSettingsRequestDataDto)
            throws ProtocolAdapterException {

        LOGGER.info("Excecuting SetRandomizationSettingsCommandExecutor");

        setRandomisationSettingsRequestDataDto.getDirectAttach();
        setRandomisationSettingsRequestDataDto.getRandomisationStartWindow();
        setRandomisationSettingsRequestDataDto.getMultiplicationFactor();
        setRandomisationSettingsRequestDataDto.getNumberOfRetries();

        return AccessResultCode.SUCCESS;

    }

}
