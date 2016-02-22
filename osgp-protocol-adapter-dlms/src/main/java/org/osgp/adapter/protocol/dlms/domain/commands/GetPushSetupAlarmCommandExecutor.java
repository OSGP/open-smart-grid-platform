/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm;

@Component()
public class GetPushSetupAlarmCommandExecutor implements CommandExecutor<Void, PushSetupAlarm> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPushSetupAlarmCommandExecutor.class);

    private static final int CLASS_ID = 40;
    private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");
    private static final int ATTRIBUTE_ID_PUSH_OBJECT_LIST = 2;
    private static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;
    private static final int ATTRIBUTE_ID_COMMUNICATION_WINDOW = 4;
    private static final int ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL = 5;
    private static final int ATTRIBUTE_ID_NUMBER_OF_RETRIES = 6;
    private static final int ATTRIBUTE_ID_REPETITION_DELAY = 7;

    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_PUSH_OBJECT_LIST),
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD),
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_COMMUNICATION_WINDOW),
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL),
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_NUMBER_OF_RETRIES),
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_REPETITION_DELAY) };

    private static final int INDEX_PUSH_OBJECT_LIST = 0;
    private static final int INDEX_SEND_DESTINATION_AND_METHOD = 1;
    private static final int INDEX_COMMUNICATION_WINDOW = 2;
    private static final int INDEX_RANDOMISATION_START_INTERVAL = 3;
    private static final int INDEX_NUMBER_OF_RETRIES = 4;
    private static final int INDEX_REPETITION_DELAY = 5;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public PushSetupAlarm execute(final LnClientConnection conn, final DlmsDevice device, final Void useless)
            throws ProtocolAdapterException {

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

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving Push Setup Alarm.");
        }

        if (getResultList.size() != ATTRIBUTE_ADDRESSES.length) {
            throw new ProtocolAdapterException("Expected " + ATTRIBUTE_ADDRESSES.length
                    + " GetResults while retrieving Push Setup Alarm, got " + getResultList.size());
        }
    }
}
