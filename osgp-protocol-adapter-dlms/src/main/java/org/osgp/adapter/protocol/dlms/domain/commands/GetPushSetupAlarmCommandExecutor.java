/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm;

@Component()
public class GetPushSetupAlarmCommandExecutor extends GetPushSetupCommandExecutor implements
        CommandExecutor<Void, PushSetupAlarm> {

    private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public GetPushSetupAlarmCommandExecutor() {
        super(OBIS_CODE);
    }

    @Override
    public PushSetupAlarm execute(final LnClientConnection conn, final DlmsDevice device, final Void useless)
            throws IOException, TimeoutException, ProtocolAdapterException {

        LOGGER.info("Retrieving Push Setup Alarm");

        final List<GetResult> getResultList = this.dlmsHelperService.getWithList(conn, device, ATTRIBUTE_ADDRESSES);

        checkResultList(getResultList);

        final PushSetupAlarm.Builder pushSetupAlarmBuilder = new PushSetupAlarm.Builder();
        pushSetupAlarmBuilder.logicalName(new CosemObisCode(OBIS_CODE.bytes()));

        pushSetupAlarmBuilder.pushObjectList(this.dlmsHelperService.readListOfObjectDefinition(
                getResultList.get(INDEX_PUSH_OBJECT_LIST), "Push Object List"));

        pushSetupAlarmBuilder.sendDestinationAndMethod(this.dlmsHelperService.readSendDestinationAndMethod(
                getResultList.get(INDEX_SEND_DESTINATION_AND_METHOD), "Send Destination And Method"));

        pushSetupAlarmBuilder.communicationWindow(this.dlmsHelperService.readListOfWindowElement(
                getResultList.get(INDEX_COMMUNICATION_WINDOW), "Communication Window"));

        pushSetupAlarmBuilder.randomisationStartInterval(this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_RANDOMISATION_START_INTERVAL), "Randomisation Start Interval").intValue());

        pushSetupAlarmBuilder.numberOfRetries(this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_NUMBER_OF_RETRIES), "Number of Retries").intValue());

        pushSetupAlarmBuilder.repetitionDelay(this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_REPETITION_DELAY), "Repetition Delay").intValue());

        return pushSetupAlarmBuilder.build();
    }
}
