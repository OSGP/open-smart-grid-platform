/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openmuc.jdlms.datatypes.DataObject;

import com.alliander.osgp.dto.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceType;

public class SetPushSetupCommandExecutor {

    protected static final int CLASS_ID = 40;
    protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;

    private static final Map<TransportServiceType, Integer> ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE = new EnumMap<>(
            TransportServiceType.class);

    private static final Map<MessageType, Integer> ENUM_VALUE_PER_MESSAGE_TYPE = new EnumMap<>(MessageType.class);

    static {
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.TCP, 0);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.UDP, 1);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.FTP, 2);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.SMTP, 3);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.SMS, 4);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.HDLC, 5);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.M_BUS, 6);
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.ZIG_BEE, 7);
        /*
         * Could be 200..255, use first value as long as no more information is
         * available.
         */
        ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.put(TransportServiceType.MANUFACTURER_SPECIFIC, 200);

        ENUM_VALUE_PER_MESSAGE_TYPE.put(MessageType.A_XDR_ENCODED_X_DLMS_APDU, 0);
        ENUM_VALUE_PER_MESSAGE_TYPE.put(MessageType.XML_ENCODED_X_DLMS_APDU, 1);
        /*
         * Could be 128..255, use first value as long as no more information is
         * available.
         */
        ENUM_VALUE_PER_MESSAGE_TYPE.put(MessageType.MANUFACTURER_SPECIFIC, 128);
    }

    protected DataObject buildSendDestinationAndMethodObject(final SendDestinationAndMethod sendDestinationAndMethod) {

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
        final Integer enumValue = ENUM_VALUE_PER_TRANSPORT_SERVICE_TYPE.get(transportServiceType);
        if (enumValue == null) {
            throw new AssertionError("Unknown TransportServiceType: " + transportServiceType);
        }
        return enumValue;
    }

    private int getEnumValueMessageType(final MessageType messageType) {
        final Integer enumValue = ENUM_VALUE_PER_MESSAGE_TYPE.get(messageType);
        if (enumValue == null) {
            throw new AssertionError("Unknown MessageType: " + messageType);
        }
        return enumValue;
    }
}
