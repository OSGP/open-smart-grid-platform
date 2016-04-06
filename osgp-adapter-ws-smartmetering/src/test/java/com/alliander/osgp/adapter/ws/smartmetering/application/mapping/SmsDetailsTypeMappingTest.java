/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;

public class SmsDetailsTypeMappingTest {

    // private final AdhocMapper adhocMapper = new AdhocMapper();
    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Test the mapping of a SmsDetails to a SmsDetailsType
    @Test
    public void testSmsDetailsToSmsDetailsType() {
        // build test data
        final String deviceIdentification = "id1";
        final Long smsMsgId = new Long(2);
        final String status = "ok";
        final String smsMsgAttemptStatus = "alsoOk";
        final String msgType = "sms";
        final SmsDetails smsDetails = new SmsDetails(deviceIdentification, smsMsgId, status, smsMsgAttemptStatus,
                msgType);

        // actual mapping
        final SmsDetailsType smsDetailsType = this.mapperFactory.getMapperFacade()
                .map(smsDetails, SmsDetailsType.class);

        // check mapping
        assertNotNull(smsDetailsType);

        assertEquals(deviceIdentification, smsDetailsType.getDeviceIdentification());
        // cast is needed because SmsDetailsType doesn't have a 'Long smsMsgId'
        // but a 'long smsMsgId'.
        assertEquals((long) smsMsgId, smsDetailsType.getSmsMsgId());
        assertEquals(status, smsDetailsType.getStatus());
        assertEquals(smsMsgAttemptStatus, smsDetailsType.getSmsMsgAttemptStatus());
        assertEquals(msgType, smsDetailsType.getMsgType());
    }

    // Test the mapping of a SmsDetailsType to a SmsDetails
    @Test
    public void testSmsDetailsTypeToSmsDetails() {
        // build test data
        final String deviceIdentification = "id1";
        final long smsMsgId = 2l;
        final String status = "ok";
        final String smsMsgAttemptStatus = "alsoOk";
        final String msgType = "sms";
        final SmsDetailsType smsDetailsType = new SmsDetailsType();
        smsDetailsType.setDeviceIdentification(deviceIdentification);
        smsDetailsType.setSmsMsgId(smsMsgId);
        smsDetailsType.setStatus(status);
        smsDetailsType.setSmsMsgAttemptStatus(smsMsgAttemptStatus);
        smsDetailsType.setMsgType(msgType);

        // actual mapping
        final SmsDetails smsDetails = this.mapperFactory.getMapperFacade().map(smsDetailsType, SmsDetails.class);

        // check mapping
        assertNotNull(smsDetails);
        assertEquals(deviceIdentification, smsDetails.getDeviceIdentification());
        assertEquals(Long.valueOf(smsMsgId), smsDetails.getSmsMsgId());
        assertEquals(status, smsDetails.getStatus());
        assertEquals(smsMsgAttemptStatus, smsDetails.getSmsMsgAttemptStatus());
        assertEquals(msgType, smsDetails.getMsgType());
    }

    // SmsDetails has a Long smsMsgId. SmsDetailsType has a long smsMsgId. Test
    // to see what happens when the Long is null (= default value)
    // @Test(expected = NullPointerException.class)
    @Test
    public void testSmsDetailsToSmsDetailsTypeWithNullLong() {
        // build test data
        final String deviceIdentification = "id1";
        final Long smsMsgId = null;
        final String status = "ok";
        final String smsMsgAttemptStatus = "alsoOk";
        final String msgType = "sms";
        final SmsDetails smsDetails = new SmsDetails(deviceIdentification, smsMsgId, status, smsMsgAttemptStatus,
                msgType);

        // actual mapping
        final SmsDetailsType smsDetailsType = this.mapperFactory.getMapperFacade()
                .map(smsDetails, SmsDetailsType.class);

        // check mapping
        assertNotNull(smsDetailsType);
        assertEquals(deviceIdentification, smsDetailsType.getDeviceIdentification());
        // A null Long is mapped to a long with value 0 by Orika. Is this ok?
        // AdhocMapper throws a NullPointerException when trying to map a null
        // Long to a long
        assertEquals(0, smsDetailsType.getSmsMsgId());
        assertEquals(status, smsDetailsType.getStatus());
        assertEquals(smsMsgAttemptStatus, smsDetailsType.getSmsMsgAttemptStatus());
        assertEquals(msgType, smsDetailsType.getMsgType());

    }

    // SmsDetails has a Long smsMsgId. SmsDetailsType has a long smsMsgId. Test
    // to see what happens when the long in SmsDetailsType has a default value.
    @Test
    public void testSmsDetailsTypeToSmsDetailsWithDefaultSmsMsgId() {
        // build test data
        final String deviceIdentification = "id1";
        final String status = "ok";
        final String smsMsgAttemptStatus = "alsoOk";
        final String msgType = "sms";
        final SmsDetailsType smsDetailsType = new SmsDetailsType();
        smsDetailsType.setDeviceIdentification(deviceIdentification);
        // No setter called for smsMsgId because we want a default value for
        // this variable
        smsDetailsType.setStatus(status);
        smsDetailsType.setSmsMsgAttemptStatus(smsMsgAttemptStatus);
        smsDetailsType.setMsgType(msgType);

        // actual mapping
        final SmsDetails smsDetails = this.mapperFactory.getMapperFacade().map(smsDetailsType, SmsDetails.class);

        // check mapping
        assertNotNull(smsDetails);
        assertEquals(deviceIdentification, smsDetails.getDeviceIdentification());
        assertEquals(Long.valueOf(smsDetailsType.getSmsMsgId()), smsDetails.getSmsMsgId());
        assertEquals(status, smsDetails.getStatus());
        assertEquals(smsMsgAttemptStatus, smsDetails.getSmsMsgAttemptStatus());
        assertEquals(msgType, smsDetails.getMsgType());

    }
}
