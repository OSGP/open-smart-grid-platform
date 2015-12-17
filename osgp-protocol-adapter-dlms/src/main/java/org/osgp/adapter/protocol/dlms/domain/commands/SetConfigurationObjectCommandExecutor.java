/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.openmuc.jdlms.internal.BitString;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlags;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObject;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeType;

@Component()
public class SetConfigurationObjectCommandExecutor implements CommandExecutor<ConfigurationObject, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectCommandExecutor.class);

    private static final int CLASS_ID = 1;
    private static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.3.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private ConfigurationObjectHelperService configurationObjectHelperService;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final ConfigurationObject configurationObject)
            throws IOException, ProtocolAdapterException {

        final ConfigurationObject configurationObjectOnDevice = this.retrieveConfigurationObject(conn);

        final SetRequestParameter request = this.buildRequest(configurationObject, configurationObjectOnDevice);

        return conn.set(request).get(0);
    }

    private SetRequestParameter buildRequest(final ConfigurationObject configurationObject,
            final ConfigurationObject configurationObjectOnDevice) {

        final DataObject complexData = this.buildRequestObject(configurationObject, configurationObjectOnDevice);
        LOGGER.info("Configuration object complex data: {}", this.dlmsHelperService.getDebugInfo(complexData));

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        return factory.createSetRequestParameter(complexData);
    }

    private DataObject buildRequestObject(final ConfigurationObject configurationObject,
            final ConfigurationObject configurationObjectOnDevice) {

        final LinkedList<DataObject> linkedList = new LinkedList<DataObject>();
        if (GprsOperationModeType.ALWAYS_ON.equals(configurationObject.getGprsOperationMode())) {
            linkedList.add(DataObject.newEnumerateData(1));
        } else if (GprsOperationModeType.TRIGGERED.equals(configurationObject.getGprsOperationMode())) {
            linkedList.add(DataObject.newEnumerateData(0));
        } else {
            // copy from meter if there is a set gprsoperationmode
            if (configurationObjectOnDevice.getGprsOperationMode() != null) {
                linkedList.add(DataObject
                        .newEnumerateData(configurationObjectOnDevice.getGprsOperationMode().ordinal()));
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
    private BitString getMergedFlags(final ConfigurationObject configurationObject,
            final ConfigurationObject configurationObjectOnDevice) {
        final List<ConfigurationFlag> configurationFlags = new ArrayList<ConfigurationFlag>();
        this.getNewFlags(configurationObject, configurationFlags);
        this.mergeOldFlags(configurationObjectOnDevice, configurationFlags);

        final byte[] newConfigurationObjectFlagsByteArray = this.configurationObjectHelperService
                .toByteArray(configurationFlags);
        final BitString bitString = new BitString(newConfigurationObjectFlagsByteArray, 16);
        return bitString;
    }

    private void mergeOldFlags(final ConfigurationObject configurationObjectOnDevice,
            final List<ConfigurationFlag> configurationFlags) {
        if (configurationObjectOnDevice != null) {
            for (final ConfigurationFlag configurationFlagOnDevice : configurationObjectOnDevice
                    .getConfigurationFlags().getConfigurationFlag()) {
                final ConfigurationFlag configurationFlag = this.getConfigurationFlag(configurationFlags,
                        configurationFlagOnDevice.getConfigurationFlagType());
                if (configurationFlag == null) {
                    configurationFlags.add(configurationFlagOnDevice);
                }
            }
        }
    }

    private void getNewFlags(final ConfigurationObject configurationObject,
            final List<ConfigurationFlag> configurationFlags) {
        for (final ConfigurationFlag configurationFlag : configurationObject.getConfigurationFlags()
                .getConfigurationFlag()) {
            configurationFlags.add(configurationFlag);
        }
    }

    private ConfigurationFlag getConfigurationFlag(final Collection<ConfigurationFlag> flags,
            final ConfigurationFlagType flagType) {
        for (final ConfigurationFlag configurationFlag : flags) {
            if (configurationFlag.getConfigurationFlagType().equals(flagType)) {
                return configurationFlag;
            }
        }
        return null;
    }

    private ConfigurationObject retrieveConfigurationObject(final ClientConnection conn) throws IOException,
    ProtocolAdapterException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final GetRequestParameter getRequestParameter = factory.createGetRequestParameter();

        LOGGER.info(
                "Retrieving current configuration object by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                getRequestParameter.classId(), getRequestParameter.obisCode(), getRequestParameter.attributeId());
        final List<GetResult> getResultList = conn.get(getRequestParameter);

        if (getResultList == null || getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No result received while retrieving current configuration object.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 result while retrieving current configuration object, got "
                    + getResultList.size());
        }

        if (getResultList.get(0) == null) {
            throw new ProtocolAdapterException(
                    "Expected data in result while retrieving current configuration object, got "
                            + getResultList.size());
        }

        return this.getConfigurationObject(getResultList);
    }

    private ConfigurationObject getConfigurationObject(final List<GetResult> resultList)
            throws ProtocolAdapterException {

        final DataObject resultData = resultList.get(0).resultData();
        LOGGER.info("Configuration object current complex data: {}", this.dlmsHelperService.getDebugInfo(resultData));

        final LinkedList<DataObject> linkedList = resultData.value();

        if (linkedList == null || linkedList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "Expected data in result while retrieving current configuration object, but got nothing");
        }

        final DataObject GprsOperationModeData = linkedList.get(0);
        if (GprsOperationModeData == null) {
            throw new ProtocolAdapterException(
                    "Expected Gprs operation mode data in result while retrieving current configuration object, but got nothing");
        }
        GprsOperationModeType gprsOperationMode = null;
        if (((Number) GprsOperationModeData.value()).longValue() == 1) {
            gprsOperationMode = GprsOperationModeType.ALWAYS_ON;
        } else if (((Number) GprsOperationModeData.value()).longValue() == 2) {
            gprsOperationMode = GprsOperationModeType.TRIGGERED;
        }

        final DataObject flagsData = linkedList.get(1);
        if (flagsData == null) {
            throw new ProtocolAdapterException(
                    "Expected flag bit data in result while retrieving current configuration object, but got nothing");
        }
        if (!(flagsData.value() instanceof BitString)) {
            throw new ProtocolAdapterException("Value in DataObject is not a BitString: "
                    + resultData.value().getClass().getName());
        }
        final byte[] flagByteArray = ((BitString) flagsData.value()).bitString();

        return new ConfigurationObject(gprsOperationMode, new ConfigurationFlags(
                this.configurationObjectHelperService.toConfigurationFlags(flagByteArray)));
    }

}
