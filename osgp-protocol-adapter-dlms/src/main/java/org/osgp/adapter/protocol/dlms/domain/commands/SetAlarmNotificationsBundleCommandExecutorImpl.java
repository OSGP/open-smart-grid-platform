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
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequest;

@Component()
public class SetAlarmNotificationsBundleCommandExecutorImpl extends
        BundleCommandExecutor<SetAlarmNotificationsRequest, ActionResponseDto> implements
        SetAlarmNotificationsBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetAlarmNotificationsBundleCommandExecutorImpl.class);

    @Autowired
    private SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor;

    public SetAlarmNotificationsBundleCommandExecutorImpl() {
        super(SetAlarmNotificationsRequest.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SetAlarmNotificationsRequest alarmNotificationsRequestDataDto) {

        AccessResultCode accessResultCode = null;
        try {

            accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn, device,
                    alarmNotificationsRequestDataDto.getAlarmNotifications());
            if (AccessResultCode.SUCCESS.equals(accessResultCode)) {
                return new ActionResponseDto("Set alarm notification on meter " + device.getDeviceIdentification()
                        + " was successful");
            } else {
                return new ActionResponseDto("Set alarm notification on meter " + device.getDeviceIdentification()
                        + " was not successful. Resultcode: " + accessResultCode);
            }
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error while setting alarm notifications", e);
            return new ActionResponseDto(e, "Error while setting alarm notifications");
        }
    }

    public SetAlarmNotificationsCommandExecutor getSetAlarmNotificationsCommandExecutor() {
        return this.setAlarmNotificationsCommandExecutor;
    }

    public void setSetAlarmNotificationsCommandExecutor(
            final SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor) {
        this.setAlarmNotificationsCommandExecutor = setAlarmNotificationsCommandExecutor;
    }
}
