/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetailsDto;

// To find out if mapping works bidirectional, and if null values are returned.
public class SmsDetailsMapperTest {

    // Create a default mapperFactory, use this by getting its MapperFacade and
    // calling map().
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // To test mapping from SmsDetails.class to SmsDetailsDTO.class
    @Test
    public void testSmsDetailsToSmsDetailsDTO() {

        final String deviceIdentification = "device";
        final Long smsMsgId = 123456789l;
        final String status = "ok";
        final String smsMsgAttemptStatus = "ok";
        final String msgType = "sms";

        final SmsDetails smsDetailsValueObject = new SmsDetails(deviceIdentification, smsMsgId, status,
                smsMsgAttemptStatus, msgType);
        final SmsDetailsDto smsDetailsDTO = this.mapperFactory.getMapperFacade().map(smsDetailsValueObject,
                SmsDetailsDto.class);

        assertEquals(deviceIdentification, smsDetailsDTO.getDeviceIdentification());
        assertEquals(smsMsgId, smsDetailsDTO.getSmsMsgId());
        assertEquals(status, smsDetailsDTO.getStatus());
        assertEquals(smsMsgAttemptStatus, smsDetailsDTO.getSmsMsgAttemptStatus());
        assertEquals(msgType, smsDetailsDTO.getMsgType());

    }

    // To test mapping from SmsDetailsDTO.class to SmsDetails.class
    @Test
    public void testSmsDetailsDTOToSmsDetails() {

        final String deviceIdentification = "device";
        final Long smsMsgId = 123456789l;
        final String status = "ok";
        final String smsMsgAttemptStatus = "ok";
        final String msgType = "sms";

        final SmsDetailsDto smsDetailsDTO = new SmsDetailsDto(deviceIdentification, smsMsgId, status,
                smsMsgAttemptStatus, msgType);
        final SmsDetails smsDetails = this.mapperFactory.getMapperFacade().map(smsDetailsDTO, SmsDetails.class);

        assertEquals(deviceIdentification, smsDetails.getDeviceIdentification());
        assertEquals(smsMsgId, smsDetails.getSmsMsgId());
        assertEquals(status, smsDetails.getStatus());
        assertEquals(smsMsgAttemptStatus, smsDetails.getSmsMsgAttemptStatus());
        assertEquals(msgType, smsDetails.getMsgType());
    }

    // To see if null is returned one way.
    @Test
    public void testNullSmsDetails() {

        final SmsDetails smsDetailsValueObject = null;
        final SmsDetailsDto smsDetailsDTO = this.mapperFactory.getMapperFacade().map(smsDetailsValueObject,
                SmsDetailsDto.class);

        assertNull(smsDetailsDTO);
    }

    // To see if null is returned the other way.
    @Test
    public void testNullSmsDetailsDTO() {

        final SmsDetailsDto smsDetailsDTO = null;
        final SmsDetails smsDetails = this.mapperFactory.getMapperFacade().map(smsDetailsDTO, SmsDetails.class);

        assertNull(smsDetails);
    }
}
