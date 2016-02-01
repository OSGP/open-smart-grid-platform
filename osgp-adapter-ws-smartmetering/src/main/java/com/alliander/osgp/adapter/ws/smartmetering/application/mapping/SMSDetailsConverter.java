/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SMSDetailsType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SMSDetails;

public class SMSDetailsConverter extends BidirectionalConverter<SMSDetails, SMSDetailsType> {

    @Override
    public SMSDetailsType convertTo(final SMSDetails source, final Type<SMSDetailsType> destinationType) {

        if (source == null) {
            return null;
        }

        final SMSDetailsType smsDetailsType = new ObjectFactory().createSMSDetailsType();
        smsDetailsType.setDeviceIdentification(source.getDeviceIdentification());
        smsDetailsType.setMsgType(source.getMsgType());
        smsDetailsType.setSmsMsgAttemptStatus(source.getSmsMsgAttemptStatus());
        smsDetailsType.setSmsMsgId(source.getSmsMsgId());
        smsDetailsType.setStatus(source.getStatus());

        return smsDetailsType;
    }

    @Override
    public SMSDetails convertFrom(final SMSDetailsType source, final Type<SMSDetails> destinationType) {

        if (source == null) {
            return null;
        }

        return new SMSDetails(source.getDeviceIdentification(), source.getSmsMsgId(), source.getStatus(),
                source.getSmsMsgAttemptStatus(), source.getMsgType());
    }

}
