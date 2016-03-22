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
import com.alliander.osgp.dto.valueobjects.smartmetering.MessageTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceTypeDto;

public class SendDestinationAndMethodConverter extends
        BidirectionalConverter<SendDestinationAndMethodDto, SendDestinationAndMethod> {

    @Override
    public SendDestinationAndMethod convertTo(final SendDestinationAndMethodDto source,
            final Type<SendDestinationAndMethod> destinationType) {
        if (source == null) {
            return null;
        }

        final TransportServiceType transportService = TransportServiceType.valueOf(source.getTransportService().name());
        final MessageType message = MessageType.valueOf(source.getMessage().name());
        return new SendDestinationAndMethod(transportService, source.getDestination(), message);
    }

    @Override
    public SendDestinationAndMethodDto convertFrom(final SendDestinationAndMethod source,
            final Type<SendDestinationAndMethodDto> destinationType) {
        if (source == null) {
            return null;
        }

        final TransportServiceTypeDto transportService = TransportServiceTypeDto.valueOf(source.getTransportService()
                .name());
        final MessageTypeDto message = MessageTypeDto.valueOf(source.getMessage().name());
        return new SendDestinationAndMethodDto(transportService, source.getDestination(), message);
    }
}
