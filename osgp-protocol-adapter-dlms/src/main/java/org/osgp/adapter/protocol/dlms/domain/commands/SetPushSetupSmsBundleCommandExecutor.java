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

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDataDto;

@Component()
public class SetPushSetupSmsBundleCommandExecutor extends SetPushSetupCommandExecutor implements
CommandExecutor<SetPushSetupSmsRequestDataDto, ActionValueObjectResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupSmsBundleCommandExecutor.class);

    private static final String ERROR_WHILE_PUSHING_SETUP_SMS = "Error while setting push setup sms for device: ";

    @Autowired
    private SetPushSetupSmsCommandExecutor setPushSetupSmsCommandExecutor;

    @Override
    public ActionValueObjectResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SetPushSetupSmsRequestDataDto setPushSetupSmsRequestDataDto) throws ProtocolAdapterException {

        AccessResultCode accessResultCode;
        try {
            accessResultCode = this.setPushSetupSmsCommandExecutor.execute(conn, device,
                    setPushSetupSmsRequestDataDto.getPushSetupSms());

            if (AccessResultCode.SUCCESS.equals(accessResultCode)) {
                return new ActionValueObjectResponseDto("Setting push setup sms for device: "
                        + device.getDeviceIdentification() + " was successful");
            } else {
                return new ActionValueObjectResponseDto("Setting push setup alarm for device: "
                        + device.getDeviceIdentification() + " was not successful. Resultcode: " + accessResultCode);
            }

        } catch (final ProtocolAdapterException e) {
            LOGGER.error(ERROR_WHILE_PUSHING_SETUP_SMS + device.getDeviceIdentification(), e);
            return new ActionValueObjectResponseDto(e, ERROR_WHILE_PUSHING_SETUP_SMS + device.getDeviceIdentification());
        }
    }

}
