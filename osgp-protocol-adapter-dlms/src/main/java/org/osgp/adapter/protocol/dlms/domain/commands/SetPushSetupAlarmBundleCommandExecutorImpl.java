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
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDataDto;

@Component()
public class SetPushSetupAlarmBundleCommandExecutorImpl implements SetPushSetupAlarmBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupAlarmBundleCommandExecutorImpl.class);

    private static final String ERROR_WHILE_PUSHING_SETUP_ALARM = "Error while setting push setup alarm for device: ";

    @Autowired
    private SetPushSetupAlarmCommandExecutor setPushSetupAlarmCommandExecutor;

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SetPushSetupAlarmRequestDataDto setPushSetupAlarmRequestDataDto) {

        AccessResultCode accessResultCode;
        try {
            accessResultCode = this.setPushSetupAlarmCommandExecutor.execute(conn, device,
                    setPushSetupAlarmRequestDataDto.getPushSetupAlarm());

            if (AccessResultCode.SUCCESS.equals(accessResultCode)) {
                return new ActionResponseDto("Setting push setup alarm for device: "
                        + device.getDeviceIdentification() + " was successful");
            } else {
                return new ActionResponseDto("Setting push setup alarm for device: "
                        + device.getDeviceIdentification() + " was not successful. Resultcode: " + accessResultCode);
            }

        } catch (final ProtocolAdapterException e) {
            LOGGER.error(ERROR_WHILE_PUSHING_SETUP_ALARM + device.getDeviceIdentification(), e);
            return new ActionResponseDto(e, ERROR_WHILE_PUSHING_SETUP_ALARM
                    + device.getDeviceIdentification());
        }
    }

    public SetPushSetupAlarmCommandExecutor getSetPushSetupAlarmCommandExecutor() {
        return setPushSetupAlarmCommandExecutor;
    }

    public void setSetPushSetupAlarmCommandExecutor(SetPushSetupAlarmCommandExecutor setPushSetupAlarmCommandExecutor) {
        this.setPushSetupAlarmCommandExecutor = setPushSetupAlarmCommandExecutor;
    }
}
