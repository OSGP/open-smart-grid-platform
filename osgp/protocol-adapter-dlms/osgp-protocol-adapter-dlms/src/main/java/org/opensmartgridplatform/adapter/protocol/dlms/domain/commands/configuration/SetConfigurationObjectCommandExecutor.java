/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectHelper.ATTRIBUTE_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectHelper.CLASS_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectHelper.OBIS_CODE;

import java.io.IOException;
import java.util.ArrayList;
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

    private static final List<ConfigurationFlagTypeDto> FLAGS_TYPES_FORBIDDEN_TO_SET = new ArrayList<>();

    static {
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.PO_ENABLE);
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.HLS_3_ON_P_3_ENABLE);
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.HLS_4_ON_P_3_ENABLE);
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.HLS_5_ON_P_3_ENABLE);
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.HLS_3_ON_PO_ENABLE);
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.HLS_4_ON_PO_ENABLE);
        FLAGS_TYPES_FORBIDDEN_TO_SET.add(ConfigurationFlagTypeDto.HLS_5_ON_PO_ENABLE);
    }

    @Autowired
    private ConfigurationObjectHelperService configurationObjectHelperService;

    @Autowired
    private DlmsHelper dlmsHelper;

    @Autowired
    private GetConfigurationObjectHelper getConfigurationObjectHelper;

    public SetConfigurationObjectCommandExecutor() {
        super(SetConfigurationObjectRequestDataDto.class);
    }

    @Override
    public ConfigurationObjectDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestDataDto =
                (SetConfigurationObjectRequestDataDto) bundleInput;

        return setConfigurationObjectRequestDataDto.getConfigurationObject();
    }

    @Override
    public ActionResponseDto asBundleResponse(final AccessResultCode executionResult) throws ProtocolAdapterException {

        this.checkAccessResultCode(executionResult);

        return new ActionResponseDto("Set configuration object was successful");
    }

    @Override
    public AccessResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final ConfigurationObjectDto configurationObject) throws ProtocolAdapterException {

        try {
            final ConfigurationObjectDto configurationObjectOnDevice = this.getConfigurationObjectHelper
                    .getConfigurationObjectDto(conn);

            final SetParameter setParameter = this.buildSetParameter(configurationObject, configurationObjectOnDevice);

            conn.getDlmsMessageListener().setDescription(
                    "SetConfigurationObject, set attribute: " + JdlmsObjectToStringUtil
                            .describeAttributes(new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID)));

            return conn.getConnection().set(setParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private SetParameter buildSetParameter(final ConfigurationObjectDto configurationObject,
            final ConfigurationObjectDto configurationObjectOnDevice) {

        final AttributeAddress configurationObjectValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final DataObject complexData = this.buildSetParameterData(configurationObject, configurationObjectOnDevice);
        LOGGER.info("Configuration object complex data: {}", this.dlmsHelper.getDebugInfo(complexData));

        return new SetParameter(configurationObjectValue, complexData);
    }

    private DataObject buildSetParameterData(final ConfigurationObjectDto configurationObject,
            final ConfigurationObjectDto configurationObjectOnDevice) {

        final List<DataObject> linkedList = new LinkedList<>();
        if (configurationObject.getGprsOperationMode() != null) {
            linkedList.add(DataObject.newEnumerateData(configurationObject.getGprsOperationMode().getValue()));
        } else {
            // copy from meter if there is a set gprsoperationmode
            if (configurationObjectOnDevice.getGprsOperationMode() != null) {
                linkedList.add(DataObject
                        .newEnumerateData(configurationObjectOnDevice.getGprsOperationMode().getValue()));
            }
        }

        final BitString bitString = this.getMergedFlags(configurationObject, configurationObjectOnDevice);
        final DataObject newBitStringData = DataObject.newBitStringData(bitString);
        linkedList.add(newBitStringData);

        return DataObject.newStructureData(linkedList);
    }

    /*
     * Merging flags to a new list of flags is done according the rule of (1) a
     * new flag setting in the request, overrules existing flag setting on the
     * meter (2) flag settings not present in the request are copied from the
     * flag settings on the meter
     */
    private BitString getMergedFlags(final ConfigurationObjectDto configurationObject,
            final ConfigurationObjectDto configurationObjectOnDevice) {
        final List<ConfigurationFlagDto> configurationFlags = this.getNewFlags(configurationObject);
        this.mergeOldFlags(configurationObjectOnDevice, configurationFlags);

        final byte[] newConfigurationObjectFlagsByteArray = this.configurationObjectHelperService
                .toByteArray(configurationFlags);

        return new BitString(newConfigurationObjectFlagsByteArray, 16);
    }

    private void mergeOldFlags(final ConfigurationObjectDto configurationObjectOnDevice,
            final List<ConfigurationFlagDto> configurationFlags) {
        if (configurationObjectOnDevice != null) {
            for (final ConfigurationFlagDto configurationFlagOnDevice : configurationObjectOnDevice
                    .getConfigurationFlags().getConfigurationFlag()) {
                final ConfigurationFlagDto configurationFlag = this
                        .getConfigurationFlag(configurationFlags, configurationFlagOnDevice.getConfigurationFlagType());
                if (configurationFlag == null) {
                    configurationFlags.add(configurationFlagOnDevice);
                }
            }
        }
    }

    private List<ConfigurationFlagDto> getNewFlags(final ConfigurationObjectDto configurationObject) {
        final List<ConfigurationFlagDto> configurationFlags = new ArrayList<>();
        if (configurationObject.getConfigurationFlags() == null) {
            return configurationFlags;
        }
        for (final ConfigurationFlagDto configurationFlag : configurationObject.getConfigurationFlags()
                .getConfigurationFlag()) {
            if (!this.isForbidden(configurationFlag.getConfigurationFlagType())) {
                configurationFlags.add(configurationFlag);
            }
        }
        return configurationFlags;
    }

    /**
     * Check if the configuratioFlag is forbidden. Check is done against the
     * list of forbidden flag types
     *
     * @param configurationFlagType
     *         the flag to check
     *
     * @return true if the flag is forbidden, else false
     */
    private boolean isForbidden(final ConfigurationFlagTypeDto configurationFlagType) {
        for (final ConfigurationFlagTypeDto forbiddenFlagType : FLAGS_TYPES_FORBIDDEN_TO_SET) {
            if (forbiddenFlagType.equals(configurationFlagType)) {
                return true;
            }
        }
        return false;
    }

    private ConfigurationFlagDto getConfigurationFlag(final Collection<ConfigurationFlagDto> flags,
            final ConfigurationFlagTypeDto flagType) {
        for (final ConfigurationFlagDto configurationFlag : flags) {
            if (configurationFlag.getConfigurationFlagType().equals(flagType)) {
                return configurationFlag;
            }
        }
        return null;
    }

}
