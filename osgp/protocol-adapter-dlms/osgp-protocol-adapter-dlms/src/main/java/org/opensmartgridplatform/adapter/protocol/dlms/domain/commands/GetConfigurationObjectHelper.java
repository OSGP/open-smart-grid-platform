/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetConfigurationObjectHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationObjectHelper.class);

    public static final int CLASS_ID = InterfaceClass.DATA.id();
    public static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.3.255");
    public static final int ATTRIBUTE_ID = DataAttribute.VALUE.attributeId();

    public static final int NUMBER_OF_CONFIGURATION_OBJECT_ELEMENTS = 2;
    public static final int INDEX_OF_GPRS_OPERATION_MODE = 0;
    public static final int INDEX_OF_CONFIGURATION_FLAGS = 1;

    @Autowired
    private ConfigurationObjectHelperService configurationObjectHelperService;

    private final DlmsHelper dlmsHelper = new DlmsHelper();

    public ConfigurationObjectDto getConfigurationObjectDto(final DlmsConnectionManager conn)
            throws ProtocolAdapterException {

        try {
            return this.retrieveConfigurationObject(conn);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private ConfigurationObjectDto retrieveConfigurationObject(final DlmsConnectionManager conn)
            throws IOException, ProtocolAdapterException {

        final AttributeAddress configurationObjectValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription(
                "retrieve current ConfigurationObject, attribute: " + JdlmsObjectToStringUtil
                        .describeAttributes(configurationObjectValue));

        LOGGER.info("Retrieving current configuration object by issuing get request for class id: {}, obis code: {}, "
                + "attribute id: {}", CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final GetResult getResult = conn.getConnection().get(configurationObjectValue);

        if (getResult == null) {
            throw new ProtocolAdapterException("No result received while retrieving current configuration object.");
        } else if (getResult.getResultCode() != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException(
                    "Non-sucessful result received retrieving configuration object: " + getResult.getResultCode());
        }

        return this.getConfigurationObject(getResult);
    }

    private ConfigurationObjectDto getConfigurationObject(final GetResult result) throws ProtocolAdapterException {

        final DataObject resultData = result.getResultData();
        if (resultData == null || !resultData.isComplex()) {
            LOGGER.warn("Configuration object result data is not complex: {}", resultData);
            throw new ProtocolAdapterException("No complex result data received retrieving configuration object.");
        }
        LOGGER.info("Configuration object current complex data: {}", this.dlmsHelper.getDebugInfo(resultData));
        final List<DataObject> elements = resultData.getValue();

        if (elements == null || elements.size() != NUMBER_OF_CONFIGURATION_OBJECT_ELEMENTS) {
            LOGGER.warn("Unexpected configuration object structure elements: {}", elements);
            throw new ProtocolAdapterException("Expected configuration object result data structure to have "
                    + NUMBER_OF_CONFIGURATION_OBJECT_ELEMENTS + " elements, but got " + (
                    elements == null ? 0 : elements.size()));
        }

        final GprsOperationModeTypeDto gprsOperationMode = this
                .getGprsOperationModeType(elements.get(INDEX_OF_GPRS_OPERATION_MODE));
        final ConfigurationFlagsDto configurationFlags = this
                .getConfigurationFlags(elements.get(INDEX_OF_CONFIGURATION_FLAGS));
        return new ConfigurationObjectDto(gprsOperationMode, configurationFlags);
    }

    private GprsOperationModeTypeDto getGprsOperationModeType(final DataObject gprsOperationModeData)
            throws ProtocolAdapterException {

        if (gprsOperationModeData == null || !gprsOperationModeData.isNumber()) {
            LOGGER.warn("GPRS operation mode data is not numerical: {}", gprsOperationModeData);
            throw new ProtocolAdapterException(
                    "Expected enum data as GPRS operation mode of configuration object structure elements, but got: "
                            + (gprsOperationModeData == null ? "null" : gprsOperationModeData.getType()));
        }
        final GprsOperationModeTypeDto gprsOperationMode;
        final Number number = gprsOperationModeData.getValue();
        if (number.intValue() == GprsOperationModeTypeDto.ALWAYS_ON.getValue()) {
            gprsOperationMode = GprsOperationModeTypeDto.ALWAYS_ON;
        } else if (number.intValue() == GprsOperationModeTypeDto.TRIGGERED.getValue()) {
            gprsOperationMode = GprsOperationModeTypeDto.TRIGGERED;
        } else {
            LOGGER.warn("Expected GPRS operation mode value to be one of [{}, {}], but got: {}",
                    GprsOperationModeTypeDto.ALWAYS_ON.getValue(), GprsOperationModeTypeDto.TRIGGERED.getValue(),
                    number);
            gprsOperationMode = null;
        }

        return gprsOperationMode;
    }

    private ConfigurationFlagsDto getConfigurationFlags(final DataObject flagsData) throws ProtocolAdapterException {
        if (flagsData == null || !flagsData.isBitString()) {
            LOGGER.warn("Configuration flags data is not a BitString: {}", flagsData);
            throw new ProtocolAdapterException(
                    "Expected bit-string data as flags of configuration object structure elements, but got: " + (
                            flagsData == null ? "null" : flagsData.getType()));
        }

        final BitString bitString = flagsData.getValue();
        final byte[] flagByteArray = bitString.getBitString();

        final List<ConfigurationFlagDto> listConfigurationFlag = this.configurationObjectHelperService
                .toConfigurationFlags(flagByteArray);

        return new ConfigurationFlagsDto(listConfigurationFlag);
    }
}
