/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetailsDto;

public class SmsDetailsConverter extends BidirectionalConverter<SmsDetailsDto, SmsDetails> {

    @Override
    public SmsDetails convertTo(final SmsDetailsDto source, final Type<SmsDetails> destinationType) {

        if (source == null) {
            return null;
        }
        return new SmsDetails(source.getDeviceIdentification(), source.getSmsMsgId(), source.getStatus(),
                source.getSmsMsgAttemptStatus(), source.getMsgType());
    }

    @Override
    public SmsDetailsDto convertFrom(final SmsDetails source, final Type<SmsDetailsDto> destinationType) {

        if (source == null) {
            return null;
        }
        return new SmsDetailsDto(source.getDeviceIdentification(), source.getSmsMsgId(), source.getStatus(),
                source.getSmsMsgAttemptStatus(), source.getMsgType());
    }

}
