/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import java.util.List;

import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetConfigurationObjectServiceDsmr4 extends GetConfigurationObjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationObjectServiceDsmr4.class);

    private static final int NUMBER_OF_CONFIGURATION_OBJECT_ELEMENTS = 2;
    private static final int INDEX_OF_GPRS_OPERATION_MODE = 0;
    private static final int INDEX_OF_CONFIGURATION_FLAGS = 1;

    private final DlmsHelper dlmsHelper;

    public GetConfigurationObjectServiceDsmr4(final DlmsHelper dlmsHelper) {
        this.dlmsHelper = dlmsHelper;
    }

    @Override
    public boolean handles(final Protocol protocol) {
        return protocol == Protocol.DSMR_4_2_2;
    }

    @Override
    ConfigurationObjectDto getConfigurationObject(final GetResult result) throws ProtocolAdapterException {

        final DataObject resultData = result.getResultData();
        if (resultData == null || !resultData.isComplex()) {
            LOGGER.warn("Configuration object result data is not complex: {}", resultData);
            throw new ProtocolAdapterException("No complex result data received retrieving configuration object.");
        }
        LOGGER.info("Configuration object current complex data: {}", this.dlmsHelper.getDebugInfo(resultData));
        final List<DataObject> elements = resultData.getValue();

        if (elements == null || elements.size() != NUMBER_OF_CONFIGURATION_OBJECT_ELEMENTS) {
            LOGGER.warn("Unexpected configuration object structure elements: {}", elements);
            throw new ProtocolAdapterException(
                    String.format("Expected configuration object result data structure to have %d elements, but got %s",
                            NUMBER_OF_CONFIGURATION_OBJECT_ELEMENTS, elements == null ? "null" : elements.size()));
        }

        final GprsOperationModeTypeDto gprsOperationMode = this.getGprsOperationModeType(
                elements.get(INDEX_OF_GPRS_OPERATION_MODE));
        final ConfigurationFlagsDto configurationFlags = this.getConfigurationFlags(
                elements.get(INDEX_OF_CONFIGURATION_FLAGS));
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

        final List<ConfigurationFlagDto> listConfigurationFlag = this.toConfigurationFlagDtos(flagByteArray);

        return new ConfigurationFlagsDto(listConfigurationFlag);
    }
}
