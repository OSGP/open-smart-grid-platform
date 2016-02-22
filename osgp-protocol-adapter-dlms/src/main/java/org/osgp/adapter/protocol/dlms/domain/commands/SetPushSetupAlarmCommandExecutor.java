/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm;

@Component()
public class SetPushSetupAlarmCommandExecutor extends SetPushSetupCommandExecutor implements
CommandExecutor<PushSetupAlarm, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupAlarmCommandExecutor.class);
    private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final LnClientConnection conn, final DlmsDevice device,
            final PushSetupAlarm pushSetupAlarm) throws IOException, TimeoutException, ProtocolAdapterException {

        final SetParameter setParameterSendDestinationAndMethod;

        if (pushSetupAlarm.hasPushObjectList()) {
            LOGGER.warn("Setting Push Object List of Push Setup Alarm not implemented: {}",
                    pushSetupAlarm.getPushObjectList());
        }

        if (pushSetupAlarm.hasSendDestinationAndMethod()) {
            final AttributeAddress sendDestinationAndMethodAddress = new AttributeAddress(CLASS_ID, OBIS_CODE,
                    ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
            final DataObject value = this.buildSendDestinationAndMethodObject(pushSetupAlarm
                    .getSendDestinationAndMethod());
            setParameterSendDestinationAndMethod = new SetParameter(sendDestinationAndMethodAddress, value);
        } else {
            LOGGER.error("Send Destination and Method of the Push Setup Alarm is expected to be set.");
            setParameterSendDestinationAndMethod = null;
        }

        if (pushSetupAlarm.hasCommunicationWindow()) {
            LOGGER.warn("Setting Communication Window of Push Setup Alarm not implemented: {}",
                    pushSetupAlarm.getCommunicationWindow());
        }
        if (pushSetupAlarm.hasRandomisationStartInterval()) {
            LOGGER.warn("Setting Randomisation Start Interval of Push Setup Alarm not implemented: {}",
                    pushSetupAlarm.getRandomisationStartInterval());
        }
        if (pushSetupAlarm.hasNumberOfRetries()) {
            LOGGER.warn("Setting Number of Retries of Push Setup Alarm not implemented: {}",
                    pushSetupAlarm.getNumberOfRetries());
        }
        if (pushSetupAlarm.hasRepetitionDelay()) {
            LOGGER.warn("Setting Repetition Delay of Push Setup Alarm not implemented: {}",
                    pushSetupAlarm.getRepetitionDelay());
        }

        if (setParameterSendDestinationAndMethod == null) {
            return AccessResultCode.OTHER_REASON;
        }
        return conn.set(setParameterSendDestinationAndMethod).get(0);
    }
}
