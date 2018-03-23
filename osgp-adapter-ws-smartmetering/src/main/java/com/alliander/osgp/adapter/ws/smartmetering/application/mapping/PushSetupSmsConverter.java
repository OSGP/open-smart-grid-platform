/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.math.BigInteger;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class PushSetupSmsConverter extends
        BidirectionalConverter<PushSetupSms, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms convertTo(
            final PushSetupSms source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }
        if (!source.hasSendDestinationAndMethod()) {
            throw new IllegalArgumentException("Unable to map PushSetup Sms without SendDestinationAndMethod.");
        }
        final SendDestinationAndMethod sendDestinationAndMethod = source.getSendDestinationAndMethod();
        final String destination = sendDestinationAndMethod.getDestination();
        if (!destination.matches("\\S++:\\d++")) {
            throw new IllegalArgumentException("Unable to parse destination as \"<host>:<port>\": " + destination);
        }
        final String[] hostAndPort = destination.split(":");
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms pushSetup = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms();
        pushSetup.setHost(hostAndPort[0]);
        pushSetup.setPort(new BigInteger(hostAndPort[1]));
        return pushSetup;
    }

    @Override
    public PushSetupSms convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms source,
            final Type<PushSetupSms> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }
        final PushSetupSms.Builder builder = new PushSetupSms.Builder();

        final String destination = source.getHost() + ":" + source.getPort();
        final SendDestinationAndMethod sendDestinationAndMethod = new SendDestinationAndMethod(TransportServiceType.TCP,
                destination, MessageType.MANUFACTURER_SPECIFIC);
        builder.withSendDestinationAndMethod(sendDestinationAndMethod);
        return builder.build();
    }
}
