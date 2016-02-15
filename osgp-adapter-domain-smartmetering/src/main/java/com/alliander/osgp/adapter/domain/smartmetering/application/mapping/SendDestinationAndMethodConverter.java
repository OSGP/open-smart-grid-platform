/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;

public class SendDestinationAndMethodConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod, SendDestinationAndMethod> {

    @Override
    public SendDestinationAndMethod convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod source,
            final Type<SendDestinationAndMethod> destinationType) {
        if (source == null) {
            return null;
        }

        final TransportServiceType transportService = TransportServiceType.valueOf(source.getTransportService().name());
        final MessageType message = MessageType.valueOf(source.getMessage().name());
        return new SendDestinationAndMethod(transportService, source.getDestination(), message);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod convertFrom(
            final SendDestinationAndMethod source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceType transportService = com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceType
                .valueOf(source.getTransportService().name());
        final com.alliander.osgp.dto.valueobjects.smartmetering.MessageType message = com.alliander.osgp.dto.valueobjects.smartmetering.MessageType
                .valueOf(source.getMessage().name());
        return new com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod(transportService,
                source.getDestination(), message);
    }
}
