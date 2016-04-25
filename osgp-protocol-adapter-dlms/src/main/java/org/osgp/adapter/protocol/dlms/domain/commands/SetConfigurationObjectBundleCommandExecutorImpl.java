/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;

@Component()
public class SetConfigurationObjectBundleCommandExecutorImpl extends
        BundleCommandExecutor<SetConfigurationObjectRequestDataDto, ActionResponseDto> implements
        SetConfigurationObjectBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectBundleCommandExecutorImpl.class);

    private static final String ERROR_WHILE_SETTING_CONFIGURATION_OBJECT_FOR_DEVICE = "Error while setting configuration object for device: ";
    private static final String VISUAL_SEPARATOR = "******************************************************";

    @Autowired
    private SetConfigurationObjectCommandExecutor setConfigurationObjectCommandExecutor;

    public SetConfigurationObjectBundleCommandExecutorImpl() {
        super(SetConfigurationObjectRequestDataDto.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestDataDto) {

        // Configuration Object towards the Smart Meter
        final ConfigurationObjectDto configurationObject = setConfigurationObjectRequestDataDto
                .getConfigurationObject();

        final GprsOperationModeTypeDto gprsOperationModeType = configurationObject.getGprsOperationMode();
        final ConfigurationFlagsDto configurationFlags = configurationObject.getConfigurationFlags();

        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("******** Configuration Object: 0-0:94.31.3.255 *******");
        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("Operation mode:{} ", gprsOperationModeType.value());
        LOGGER.info("Flags:");

        for (final ConfigurationFlagDto configurationFlag : configurationFlags.getConfigurationFlag()) {
            LOGGER.info("Flag : {}, enabled = {}", configurationFlag.getConfigurationFlagType().toString(),
                    configurationFlag.isEnabled());
        }
        LOGGER.info(VISUAL_SEPARATOR);

        AccessResultCode accessResultCode;
        try {
            accessResultCode = this.setConfigurationObjectCommandExecutor.execute(conn, device, configurationObject);

            if (AccessResultCode.SUCCESS.equals(accessResultCode)) {
                return new ActionResponseDto("Setting configuration object for device: "
                        + device.getDeviceIdentification() + " was successful");
            } else {
                return new ActionResponseDto("Setting configuration object for device: "
                        + device.getDeviceIdentification() + " was not successful. Resultcode: " + accessResultCode);
            }

        } catch (final ProtocolAdapterException e) {
            LOGGER.error(ERROR_WHILE_SETTING_CONFIGURATION_OBJECT_FOR_DEVICE + device.getDeviceIdentification(), e);
            return new ActionResponseDto(e, ERROR_WHILE_SETTING_CONFIGURATION_OBJECT_FOR_DEVICE
                    + device.getDeviceIdentification());
        }
    }

    public SetConfigurationObjectCommandExecutor getSetConfigurationObjectCommandExecutor() {
        return this.setConfigurationObjectCommandExecutor;
    }

    public void setSetConfigurationObjectCommandExecutor(
            final SetConfigurationObjectCommandExecutor setConfigurationObjectCommandExecutor) {
        this.setConfigurationObjectCommandExecutor = setConfigurationObjectCommandExecutor;
    }
}
