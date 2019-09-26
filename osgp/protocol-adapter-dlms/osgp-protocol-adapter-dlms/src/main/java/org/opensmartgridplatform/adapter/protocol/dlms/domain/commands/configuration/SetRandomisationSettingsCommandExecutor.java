/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import java.io.IOException;
import java.util.Optional;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
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

    private final DlmsObjectConfigService dlmsObjectConfigService;

    @Autowired
    public SetRandomisationSettingsCommandExecutor(DlmsObjectConfigService dlmsObjectConfigService) {
        super(SetRandomisationSettingsRequestDataDto.class);
        this.dlmsObjectConfigService = dlmsObjectConfigService;
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

        LOGGER.info("Excecuting SetRandomizationSettingsCommandExecutor {}, {}, {}, {} ",
                setRandomisationSettingsRequestDataDto.getDirectAttach(),
                setRandomisationSettingsRequestDataDto.getRandomisationStartWindow(),
                setRandomisationSettingsRequestDataDto.getMultiplicationFactor(),
                setRandomisationSettingsRequestDataDto.getNumberOfRetries());

        int directAttach = setRandomisationSettingsRequestDataDto.getDirectAttach();
        int randomisationStartWindow = setRandomisationSettingsRequestDataDto.getRandomisationStartWindow();
        int multiplicationFactor = setRandomisationSettingsRequestDataDto.getMultiplicationFactor();
        int numberOfRetries = setRandomisationSettingsRequestDataDto.getNumberOfRetries();

        AttributeAddress directAttachAddress = getAttributeAddress(device, DlmsObjectType.DIRECT_ATTACH);
        AttributeAddress randomisationSettingsAddress = getAttributeAddress(device,
                DlmsObjectType.RANDOMISATION_SETTINGS);

        DataObject directAttachObject = DataObject.newBoolData(directAttach == 1 ? Boolean.TRUE : Boolean.FALSE);

        DataObject randomisationStartWindowObject = DataObject.newUInteger32Data(randomisationStartWindow);
        DataObject multiplicationFactorObject = DataObject.newUInteger16Data(multiplicationFactor);
        DataObject numberOfRetriesObject = DataObject.newUInteger16Data(numberOfRetries);

        DataObject randomisationSettingsObject = DataObject.newStructureData(randomisationStartWindowObject,
                multiplicationFactorObject, numberOfRetriesObject);

        final SetParameter setParameterDirectAttach = new SetParameter(directAttachAddress, directAttachObject);
        final SetParameter setRandomisationSettings = new SetParameter(randomisationSettingsAddress,
                randomisationSettingsObject);

        writeAttribute(conn, setParameterDirectAttach, "setParameterDirectAttach");
        writeAttribute(conn, setParameterDirectAttach, "setRandomisationSettings");

        return AccessResultCode.SUCCESS;

    }

    private void writeAttribute(final DlmsConnectionManager conn, final SetParameter parameter,
            final String attributeName) throws ProtocolAdapterException {
        try {
            final AccessResultCode result = conn.getConnection().set(parameter);
            if (!result.equals(AccessResultCode.SUCCESS)) {
                throw new ProtocolAdapterException(String.format(
                        "Attribute '%s' of the clock configuration was not set successfully. ResultCode: %s",
                        attributeName, result.name()));
            }
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private AttributeAddress getAttributeAddress(final DlmsDevice device, final DlmsObjectType dlmsObjectType)
            throws ProtocolAdapterException {
        final Optional<AttributeAddress> attributeAddress = this.dlmsObjectConfigService.findAttributeAddress(device,
                DlmsObjectType.ALARM_FILTER, null);
        return attributeAddress.orElseThrow(
                () -> new ProtocolAdapterException("Could not find any configuration for " + dlmsObjectType));
    }

}
