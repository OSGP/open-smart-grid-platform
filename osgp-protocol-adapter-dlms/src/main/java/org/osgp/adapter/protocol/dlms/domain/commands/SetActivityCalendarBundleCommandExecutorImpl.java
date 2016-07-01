/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDataDto;

@Component()
public class SetActivityCalendarBundleCommandExecutorImpl extends
BundleCommandExecutor<ActivityCalendarDataDto, ActionResponseDto> implements
SetActivityCalendarBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarBundleCommandExecutorImpl.class);

    @Autowired
    private SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor;

    public SetActivityCalendarBundleCommandExecutorImpl() {
        super(ActivityCalendarDataDto.class);
    }

    @Override
    public ActionResponseDto execute(final DlmsConnection conn, final DlmsDevice device,
            final ActivityCalendarDataDto activityCalendar) {

        try {
            this.setActivityCalendarCommandExecutor.execute(conn, device, activityCalendar.getActivityCalendar());
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error while setting new activity calendar", e);
            return new ActionResponseDto(e, "Error while setting new activity calendar");
        }

        return new ActionResponseDto("Set Activity Calendar Result is OK for device id: "
                + device.getDeviceIdentification() + " calendar name: "
                + activityCalendar.getActivityCalendar().getCalendarName());
    }

    public SetActivityCalendarCommandExecutor getSetActivityCalendarCommandExecutor() {
        return this.setActivityCalendarCommandExecutor;
    }

    public void setSetActivityCalendarCommandExecutor(
            final SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor) {
        this.setActivityCalendarCommandExecutor = setActivityCalendarCommandExecutor;
    }

}
