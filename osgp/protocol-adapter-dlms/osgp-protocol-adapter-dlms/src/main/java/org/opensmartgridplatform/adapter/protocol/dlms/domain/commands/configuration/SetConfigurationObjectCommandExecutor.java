/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectServiceDsmr4.ATTRIBUTE_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectServiceDsmr4.CLASS_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectServiceDsmr4.OBIS_CODE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetConfigurationObjectCommandExecutor
        extends AbstractCommandExecutor<ConfigurationObjectDto, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectCommandExecutor.class);
    private static final int NUMBER_OF_FLAG_BITS = 16;

    @Autowired
    private DlmsHelper dlmsHelper;

    @Autowired
    private GetConfigurationObjectServiceLookup serviceLookup;

    public SetConfigurationObjectCommandExecutor() {
        super(SetConfigurationObjectRequestDataDto.class);
    }

    @Override
    public ConfigurationObjectDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {
        this.checkActionRequestType(bundleInput);
        final SetConfigurationObjectRequestDataDto dto = (SetConfigurationObjectRequestDataDto) bundleInput;
        return dto.getConfigurationObject();
    }

    @Override
    public ActionResponseDto asBundleResponse(final AccessResultCode executionResult) throws ProtocolAdapterException {
        this.checkAccessResultCode(executionResult);
        return new ActionResponseDto("Set configuration object was successful");
    }

    @Override
    public AccessResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final ConfigurationObjectDto configurationToSet) throws ProtocolAdapterException {
        try {
            final Protocol protocol = Protocol.forDevice(device);
            final GetConfigurationObjectService service = this.serviceLookup.lookupServiceForProtocol(protocol);
            final ConfigurationObjectDto configurationOnDevice = service.getConfigurationObjectDto(conn);

            final AttributeAddress configurationObjectValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

            // TODO: Construct BitString for SMR5
            final DataObject complexData = this.buildSetParameterData(configurationToSet, configurationOnDevice);
            LOGGER.info("Configuration object complex data: {}", this.dlmsHelper.getDebugInfo(complexData));

            final SetParameter setParameter = new SetParameter(configurationObjectValue, complexData);

            conn.getDlmsMessageListener().setDescription(
                    "SetConfigurationObject, set attribute: " + JdlmsObjectToStringUtil.describeAttributes(
                            configurationObjectValue));

            return conn.getConnection().set(setParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private DataObject buildSetParameterData(final ConfigurationObjectDto configurationToSet,
            final ConfigurationObjectDto configurationOnDevice) {

        final List<DataObject> dataObjects = new LinkedList<>();
        if (configurationToSet.getGprsOperationMode() != null) {
            dataObjects.add(DataObject.newEnumerateData(configurationToSet.getGprsOperationMode().getValue()));
        } else {
            // copy from meter if there is a set gprsoperationmode
            if (configurationOnDevice.getGprsOperationMode() != null) {
                dataObjects.add(DataObject.newEnumerateData(configurationOnDevice.getGprsOperationMode().getValue()));
            }
        }

        final BitString bitString = this.getMergedFlags(configurationToSet, configurationOnDevice);
        final DataObject newBitStringData = DataObject.newBitStringData(bitString);
        dataObjects.add(newBitStringData);

        return DataObject.newStructureData(dataObjects);
    }

    /*
     * Merging flags to a new list of flags is done according the rule of (1) a
     * new flag setting in the request, overrules existing flag setting on the
     * meter (2) flag settings not present in the request are copied from the
     * flag settings on the meter
     */
    private BitString getMergedFlags(final ConfigurationObjectDto configurationToSet,
            final ConfigurationObjectDto configurationOnDevice) {
        final List<ConfigurationFlagDto> configurationFlagDtos = this.getNewFlags(configurationToSet);
        this.mergeOldFlags(configurationOnDevice, configurationFlagDtos);

        final byte[] newConfigurationObjectFlagsByteArray = this.toByteArray(configurationFlagDtos);

        return new BitString(newConfigurationObjectFlagsByteArray, NUMBER_OF_FLAG_BITS);
    }

    private void mergeOldFlags(final ConfigurationObjectDto configurationOnDevice,
            final List<ConfigurationFlagDto> configurationFlagDtos) {
        if (configurationOnDevice != null) {
            for (final ConfigurationFlagDto configurationFlagOnDevice :
                    configurationOnDevice.getConfigurationFlags().getConfigurationFlag()) {
                final ConfigurationFlagDto configurationFlag = this.getConfigurationFlag(configurationFlagDtos,
                        configurationFlagOnDevice.getConfigurationFlagType());
                if (configurationFlag == null) {
                    configurationFlagDtos.add(configurationFlagOnDevice);
                }
            }
        }
    }

    private byte[] toByteArray(final List<ConfigurationFlagDto> configurationFlagDtos) {
        final BitSet bitSet = new BitSet(NUMBER_OF_FLAG_BITS);
        for (final ConfigurationFlagDto configurationFlagDto : configurationFlagDtos) {
            if (configurationFlagDto.isEnabled()) {
                // TODO: handle SMR5
                bitSet.set(configurationFlagDto.getConfigurationFlagType().getBitPositionDsmr4(), true);
            }
        }
        // 16 bits is 2 bytes
        final byte[] byteArray = bitSet.toByteArray();
        // swap bytes to set MSB first
        final byte tmp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = tmp;
        return byteArray;
    }

    private List<ConfigurationFlagDto> getNewFlags(final ConfigurationObjectDto configurationToSet) {
        final List<ConfigurationFlagDto> configurationFlagDtos = new ArrayList<>();
        if (configurationToSet.getConfigurationFlags() != null) {
            for (final ConfigurationFlagDto configurationFlagDto :
                    configurationToSet.getConfigurationFlags().getConfigurationFlag()) {
                if (!configurationFlagDto.getConfigurationFlagType().isReadOnly()) {
                    configurationFlagDtos.add(configurationFlagDto);
                }
            }
        }
        return configurationFlagDtos;
    }

    private ConfigurationFlagDto getConfigurationFlag(final Collection<ConfigurationFlagDto> configurationFlagDtos,
            final ConfigurationFlagTypeDto configurationFlagTypeDto) {
        for (final ConfigurationFlagDto configurationFlagDto : configurationFlagDtos) {
            if (configurationFlagDto.getConfigurationFlagType().equals(configurationFlagTypeDto)) {
                return configurationFlagDto;
            }
        }
        return null;
    }

}
