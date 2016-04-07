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
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDataDto;

@Component()
public class SetAlarmNotificationsBundleCommandExecutor implements
        CommandExecutor<SetAlarmNotificationsRequestDataDto, ActionValueObjectResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetAlarmNotificationsBundleCommandExecutor.class);

    @Autowired
    private SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor;

    @Override
    public ActionValueObjectResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SetAlarmNotificationsRequestDataDto alarmNotificationsRequestDataDto) {

        AccessResultCode accessResultCode = null;
        try {

            accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn, device,
                    alarmNotificationsRequestDataDto.getAlarmNotifications());
            if (AccessResultCode.SUCCESS.equals(accessResultCode)) {
                return new ActionValueObjectResponseDto("Set alarm notification on meter "
                        + device.getDeviceIdentification() + " was successful");
            } else {
                return new ActionValueObjectResponseDto("Set alarm notification on meter "
                        + device.getDeviceIdentification() + " was not successful. Resultcode: " + accessResultCode);
            }
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error while setting alarm notifications", e);
            return new ActionValueObjectResponseDto(e, "Error while setting alarm notifications");
        }

    }
}
