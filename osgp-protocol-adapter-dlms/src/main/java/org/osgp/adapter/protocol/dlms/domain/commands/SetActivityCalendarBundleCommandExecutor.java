/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.MethodResultCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDataDto;

@Component()
public class SetActivityCalendarBundleCommandExecutor implements
CommandExecutor<ActivityCalendarDataDto, ActionValueObjectResponseDto> {

    @Autowired
    private SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor;

    @Autowired
    private SetActivityCalendarCommandActivationExecutor setActivityCalendarCommandActivationExecutor;

    @Override
    public ActionValueObjectResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final ActivityCalendarDataDto activityCalendar) {

        MethodResultCode methodResult = null;

        try {
            this.setActivityCalendarCommandExecutor.execute(conn, device, activityCalendar.getActivityCalendar());

            methodResult = this.setActivityCalendarCommandActivationExecutor.execute(conn, device, null);
        } catch (final ProtocolAdapterException e) {
            return new ActionValueObjectResponseDto(e, "Error while setting new activity calendar");
        }

        if (!MethodResultCode.SUCCESS.equals(methodResult)) {
            return new ActionValueObjectResponseDto("AccessResultCode for set Activity Calendar: " + methodResult);
        }

        return new ActionValueObjectResponseDto("Set Activity Calendar Result is OK for device id: "
                + device.getDeviceIdentification() + " calendar name: "
                + activityCalendar.getActivityCalendar().getCalendarName());

    }
}
