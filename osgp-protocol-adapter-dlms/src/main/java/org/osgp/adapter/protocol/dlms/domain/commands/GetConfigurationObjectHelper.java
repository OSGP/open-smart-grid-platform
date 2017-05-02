/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.DataAttribute;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@Component()
public class GetConfigurationObjectHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationObjectHelper.class);

    public static final int CLASS_ID = InterfaceClass.DATA.id();
    public static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.3.255");
    public static final int ATTRIBUTE_ID = DataAttribute.VALUE.attributeId();

    @Autowired
    private ConfigurationObjectHelperService configurationObjectHelperService;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public ConfigurationObjectDto getConfigurationObjectDto(final DlmsConnectionHolder conn)
            throws ProtocolAdapterException {

        try {
            return this.retrieveConfigurationObject(conn);
        } catch (IOException | TimeoutException e) {
            throw new ConnectionException(e);
        }
    }

    private ConfigurationObjectDto retrieveConfigurationObject(final DlmsConnectionHolder conn)
            throws IOException, TimeoutException, ProtocolAdapterException {

        final AttributeAddress configurationObjectValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription("retrieve current ConfigurationObject, attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(configurationObjectValue));

        LOGGER.info(
                "Retrieving current configuration object by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final GetResult getResult = conn.getConnection().get(configurationObjectValue);

        if (getResult == null) {
            throw new ProtocolAdapterException("No result received while retrieving current configuration object.");
        }

        return this.getConfigurationObject(getResult);
    }

    private ConfigurationObjectDto getConfigurationObject(final GetResult result) throws ProtocolAdapterException {

        final DataObject resultData = result.getResultData();
        LOGGER.info("Configuration object current complex data: {}", this.dlmsHelperService.getDebugInfo(resultData));

        final List<DataObject> linkedList = resultData.getValue();

        if (linkedList == null || linkedList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "Expected data in result while retrieving current configuration object, but got nothing");
        }

        final GprsOperationModeTypeDto gprsOperationMode = this.getGprsOperationModeType(linkedList);
        final ConfigurationFlagsDto configurationFlags = this.getConfigurationFlags(linkedList, resultData);
        return new ConfigurationObjectDto(gprsOperationMode, configurationFlags);
    }

    private GprsOperationModeTypeDto getGprsOperationModeType(final List<DataObject> linkedList)
            throws ProtocolAdapterException {

        final DataObject gprsOperationModeData = linkedList.get(0);
        if (gprsOperationModeData == null) {
            throw new ProtocolAdapterException(
                    "Expected Gprs operation mode data in result while retrieving current configuration object, but got nothing");
        }
        GprsOperationModeTypeDto gprsOperationMode = null;
        if (((Number) gprsOperationModeData.getValue()).longValue() == 1) {
            gprsOperationMode = GprsOperationModeTypeDto.ALWAYS_ON;
        } else if (((Number) gprsOperationModeData.getValue()).longValue() == 2) {
            gprsOperationMode = GprsOperationModeTypeDto.TRIGGERED;
        }

        return gprsOperationMode;
    }

    private ConfigurationFlagsDto getConfigurationFlags(final List<DataObject> linkedList, final DataObject resultData)
            throws ProtocolAdapterException {
        final DataObject flagsData = linkedList.get(1);

        if (flagsData == null) {
            throw new ProtocolAdapterException(
                    "Expected flag bit data in result while retrieving current configuration object, but got nothing");
        }
        if (!(flagsData.getValue() instanceof BitString)) {
            throw new ProtocolAdapterException(
                    "Value in DataObject is not a BitString: " + resultData.getValue().getClass().getName());
        }
        final byte[] flagByteArray = ((BitString) flagsData.getValue()).getBitString();

        final List<ConfigurationFlagDto> listConfigurationFlag = this.configurationObjectHelperService
                .toConfigurationFlags(flagByteArray);

        return new ConfigurationFlagsDto(listConfigurationFlag);
    }
}
