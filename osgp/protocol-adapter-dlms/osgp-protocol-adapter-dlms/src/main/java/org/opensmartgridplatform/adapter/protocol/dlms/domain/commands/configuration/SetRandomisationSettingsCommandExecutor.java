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
import java.util.List;
import java.util.Optional;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
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
    private final ProtocolServiceLookup protocolServiceLookup;

    @Autowired
    public SetRandomisationSettingsCommandExecutor(DlmsObjectConfigService dlmsObjectConfigService,
            ProtocolServiceLookup protocolServiceLookup) {
        super(SetRandomisationSettingsRequestDataDto.class);
        this.dlmsObjectConfigService = dlmsObjectConfigService;
        this.protocolServiceLookup = protocolServiceLookup;
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
        return new ActionResponseDto("Set Randomisation Settings was successful.");
    }

    @Override
    public AccessResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final SetRandomisationSettingsRequestDataDto setRandomisationSettingsRequestDataDto)
            throws ProtocolAdapterException {

        LOGGER.info("Executing SetRandomisationSettingsCommandExecutor {}, {}, {}, {} ",
                setRandomisationSettingsRequestDataDto.getDirectAttach(),
                setRandomisationSettingsRequestDataDto.getRandomisationStartWindow(),
                setRandomisationSettingsRequestDataDto.getMultiplicationFactor(),
                setRandomisationSettingsRequestDataDto.getNumberOfRetries());

        boolean directAttach = setRandomisationSettingsRequestDataDto.getDirectAttach() == 1;
        int randomisationStartWindow = setRandomisationSettingsRequestDataDto.getRandomisationStartWindow();
        int multiplicationFactor = setRandomisationSettingsRequestDataDto.getMultiplicationFactor();
        int numberOfRetries = setRandomisationSettingsRequestDataDto.getNumberOfRetries();

        if (directAttach) {
            LOGGER.info("Enabling directAttach on device {}.", device.getDeviceIdentification());
            writeDirectAttach(conn, device, true);
        } else {
            LOGGER.info("Disabling directAttach and setting Randomisation Settings for device {}.",
                    device.getDeviceIdentification());
            writeDirectAttach(conn, device, false);
            writeRandomisationSettings(conn, device, randomisationStartWindow, multiplicationFactor, numberOfRetries);
        }

        return AccessResultCode.SUCCESS;

    }

    private void writeRandomisationSettings(DlmsConnectionManager conn, DlmsDevice device, int randomisationStartWindow,
            int multiplicationFactor, int numberOfRetries) throws ProtocolAdapterException {
        AttributeAddress randomisationSettingsAddress = getAttributeAddress(device);

        DataObject randomisationStartWindowObject = DataObject.newUInteger32Data(randomisationStartWindow);
        DataObject multiplicationFactorObject = DataObject.newUInteger16Data(multiplicationFactor);
        DataObject numberOfRetriesObject = DataObject.newUInteger16Data(numberOfRetries);

        DataObject randomisationSettingsObject = DataObject.newStructureData(randomisationStartWindowObject,
                multiplicationFactorObject, numberOfRetriesObject);

        final SetParameter setRandomisationSettings = new SetParameter(randomisationSettingsAddress,
                randomisationSettingsObject);
        writeAttribute(conn, setRandomisationSettings);
    }

    private void writeDirectAttach(DlmsConnectionManager conn, DlmsDevice device, boolean directAttach)
            throws ProtocolAdapterException {

        Protocol protocol = Protocol.forDevice(device);
        GetConfigurationObjectService getConfigurationObjectService = protocolServiceLookup.lookupGetService(protocol);
        ConfigurationObjectDto configurationOnDevice = getConfigurationObjectService.getConfigurationObject(conn);
        final SetConfigurationObjectService setService = this.protocolServiceLookup.lookupSetService(protocol);

        List<ConfigurationFlagDto> newConfiguration = configurationOnDevice.getConfigurationFlags().getFlags();

        newConfiguration.removeIf(
                e -> e.getConfigurationFlagType() == ConfigurationFlagTypeDto.DIRECT_ATTACH_AT_POWER_ON);

        ConfigurationFlagDto directAttachAtPowerOn = new ConfigurationFlagDto(
                ConfigurationFlagTypeDto.DIRECT_ATTACH_AT_POWER_ON, directAttach);

        newConfiguration.add(directAttachAtPowerOn);
        ConfigurationFlagsDto configurationFlagsDto = new ConfigurationFlagsDto(newConfiguration);
        ConfigurationObjectDto configurationToSet = new ConfigurationObjectDto(configurationFlagsDto);

        final AccessResultCode result = setService.setConfigurationObject(conn, configurationToSet,
                configurationOnDevice);

        checkResult(result, "directAttach");
    }

    private void writeAttribute(final DlmsConnectionManager conn, final SetParameter parameter)
            throws ProtocolAdapterException {
        try {
            final AccessResultCode result = conn.getConnection().set(parameter);
            checkResult(result, "setRandomisationSettings");
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private void checkResult(AccessResultCode result, String attributeName) throws ProtocolAdapterException {
        if (!result.equals(AccessResultCode.SUCCESS)) {
            throw new ProtocolAdapterException(String.format(
                    "Attribute '%s' of the Randomisation Settings was not set successfully. ResultCode: %s",
                    attributeName, result.name()));
        }
    }

    private AttributeAddress getAttributeAddress(final DlmsDevice device) throws ProtocolAdapterException {
        final Optional<AttributeAddress> attributeAddress = this.dlmsObjectConfigService.findAttributeAddress(device,
                DlmsObjectType.RANDOMISATION_SETTINGS, null);
        return attributeAddress.orElseThrow(() -> new ProtocolAdapterException(
                "Could not find any configuration for DlmsObjectType.RANDOMISATION_SETTINGS"));
    }

}
