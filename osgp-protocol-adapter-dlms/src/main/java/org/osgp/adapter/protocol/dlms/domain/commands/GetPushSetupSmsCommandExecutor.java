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
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSms;

@Component()
public class GetPushSetupSmsCommandExecutor extends GetPushSetupCommandExecutor implements
        CommandExecutor<Void, PushSetupSms> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPushSetupSmsCommandExecutor.class);
    private static final ObisCode OBIS_CODE = new ObisCode("0.2.25.9.0.255");

    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = new AttributeAddress[6];

    static {
        ATTRIBUTE_ADDRESSES[0] = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_PUSH_OBJECT_LIST);
        ATTRIBUTE_ADDRESSES[1] = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
        ATTRIBUTE_ADDRESSES[2] = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_COMMUNICATION_WINDOW);
        ATTRIBUTE_ADDRESSES[3] = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL);
        ATTRIBUTE_ADDRESSES[4] = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_NUMBER_OF_RETRIES);
        ATTRIBUTE_ADDRESSES[5] = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_REPETITION_DELAY);
    }

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public PushSetupSms execute(final LnClientConnection conn, final DlmsDevice device, final Void useless)
            throws IOException, TimeoutException, ProtocolAdapterException {

        LOGGER.info("Retrieving Push Setup Alarm");

        final List<GetResult> getResultList = this.dlmsHelperService.getWithList(conn, device, ATTRIBUTE_ADDRESSES);

        checkResultList(getResultList, ATTRIBUTE_ADDRESSES);

        final PushSetupSms.Builder pushSetupSmsBuilder = new PushSetupSms.Builder();
        pushSetupSmsBuilder.logicalName(new CosemObisCode(OBIS_CODE.bytes()));

        pushSetupSmsBuilder.pushObjectList(this.dlmsHelperService.readListOfObjectDefinition(
                getResultList.get(INDEX_PUSH_OBJECT_LIST), "Push Object List"));

        pushSetupSmsBuilder.sendDestinationAndMethod(this.dlmsHelperService.readSendDestinationAndMethod(
                getResultList.get(INDEX_SEND_DESTINATION_AND_METHOD), "Send Destination And Method"));

        pushSetupSmsBuilder.communicationWindow(this.dlmsHelperService.readListOfWindowElement(
                getResultList.get(INDEX_COMMUNICATION_WINDOW), "Communication Window"));

        pushSetupSmsBuilder.randomisationStartInterval(this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_RANDOMISATION_START_INTERVAL), "Randomisation Start Interval").intValue());

        pushSetupSmsBuilder.numberOfRetries(this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_NUMBER_OF_RETRIES), "Number of Retries").intValue());

        pushSetupSmsBuilder.repetitionDelay(this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_REPETITION_DELAY), "Repetition Delay").intValue());

        return pushSetupSmsBuilder.build();
    }
}
