/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

import com.alliander.osgp.dto.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceType;

@Component()
public class SetPushSetupAlarmCommandExecutor implements CommandExecutor<PushSetupAlarm, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupAlarmCommandExecutor.class);

    private static final int CLASS_ID = 40;
    private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");
    private static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final LnClientConnection conn, final DlmsDevice device,
            final PushSetupAlarm pushSetupAlarm) throws IOException, TimeoutException, ProtocolAdapterException {

        final SetParameter setParameterSendDestinationAndMethod;

        if (pushSetupAlarm.hasPushObjectList()) {
            LOGGER.warn("Setting Push Object List of Push Setup Alarm not implemented: "
                    + pushSetupAlarm.getPushObjectList());
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
            LOGGER.warn("Setting Communication Window of Push Setup Alarm not implemented: "
                    + pushSetupAlarm.getCommunicationWindow());
        }
        if (pushSetupAlarm.hasRandomisationStartInterval()) {
            LOGGER.warn("Setting Randomisation Start Interval of Push Setup Alarm not implemented: "
                    + pushSetupAlarm.getRandomisationStartInterval());
        }
        if (pushSetupAlarm.hasNumberOfRetries()) {
            LOGGER.warn("Setting Number of Retries of Push Setup Alarm not implemented: "
                    + pushSetupAlarm.getNumberOfRetries());
        }
        if (pushSetupAlarm.hasRepetitionDelay()) {
            LOGGER.warn("Setting Repetition Delay of Push Setup Alarm not implemented: "
                    + pushSetupAlarm.getRepetitionDelay());
        }

        if (setParameterSendDestinationAndMethod == null) {
            return AccessResultCode.OTHER_REASON;
        }
        return conn.set(setParameterSendDestinationAndMethod).get(0);
    }

    private DataObject buildSendDestinationAndMethodObject(final SendDestinationAndMethod sendDestinationAndMethod) {

        final List<DataObject> sendDestinationAndMethodElements = new ArrayList<>();

        // add service
        final int enumValueTransportServiceType = this.getEnumValueTransportServiceType(sendDestinationAndMethod
                .getTransportService());
        sendDestinationAndMethodElements.add(DataObject.newEnumerateData(enumValueTransportServiceType));

        // add destination
        sendDestinationAndMethodElements.add(DataObject.newOctetStringData(sendDestinationAndMethod.getDestination()
                .getBytes(StandardCharsets.US_ASCII)));

        // add message
        final int enumValueMessageType = this.getEnumValueMessageType(sendDestinationAndMethod.getMessage());
        sendDestinationAndMethodElements.add(DataObject.newEnumerateData(enumValueMessageType));

        return DataObject.newStructureData(sendDestinationAndMethodElements);
    }

    private int getEnumValueTransportServiceType(final TransportServiceType transportServiceType) {
        final short enumValue;
        switch (transportServiceType) {
        case TCP:
            enumValue = 0;
            break;
        case UDP:
            enumValue = 1;
            break;
        case FTP:
            enumValue = 2;
            break;
        case SMTP:
            enumValue = 3;
            break;
        case SMS:
            enumValue = 4;
            break;
        case HDLC:
            enumValue = 5;
            break;
        case M_BUS:
            enumValue = 6;
            break;
        case ZIG_BEE:
            enumValue = 7;
            break;
        case MANUFACTURER_SPECIFIC:
            /*
             * Could be 200..255, use first value as long as no more information
             * is available.
             */
            enumValue = 200;
            break;
        default:
            throw new AssertionError("Unknown TransportServiceType: " + transportServiceType);
        }
        return enumValue;
    }

    private int getEnumValueMessageType(final MessageType messageType) {
        final short enumValue;
        switch (messageType) {
        case A_XDR_ENCODED_X_DLMS_APDU:
            enumValue = 0;
            break;
        case XML_ENCODED_X_DLMS_APDU:
            enumValue = 1;
            break;
        case MANUFACTURER_SPECIFIC:
            /*
             * Could be 128..255, use first value as long as no more information
             * is available.
             */
            enumValue = 128;
            break;
        default:
            throw new AssertionError("Unknown MessageType: " + messageType);
        }
        return enumValue;
    }
}
