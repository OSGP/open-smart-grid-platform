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
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupAlarmMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // This mapping needs it's converter.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new PushSetupAlarmConverter());
    }

    // Test if mapping a PushSetupAlarm object succeeds.
    @Test
    public void testPushSetupAlarmMapping() {
        // build test data
        final String host = "host";
        final BigInteger port = BigInteger.TEN;
        final PushSetupAlarm pushSetupAlarmOriginal = new PushSetupAlarm();
        pushSetupAlarmOriginal.setHost(host);
        pushSetupAlarmOriginal.setPort(port);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm pushSetupAlarmMapped = this.mapperFactory
                .getMapperFacade().map(pushSetupAlarmOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);

        // check mapping
        assertNotNull(pushSetupAlarmMapped);
        // Only a SendDestinationAndMethod is mapped:
        assertNull(pushSetupAlarmMapped.getLogicalName());
        assertNull(pushSetupAlarmMapped.getCommunicationWindow());
        assertNull(pushSetupAlarmMapped.getNumberOfRetries());
        assertNull(pushSetupAlarmMapped.getPushObjectList());
        assertNull(pushSetupAlarmMapped.getRandomisationStartInterval());
        assertNull(pushSetupAlarmMapped.getRepetitionDelay());

        // port and host are combined into destination. The converter sets
        // default values for the other two variables of a
        // SendDestinationAndMethod.
        assertEquals(host + ":" + port, pushSetupAlarmMapped.getSendDestinationAndMethod().getDestination());
        assertEquals(TransportServiceType.TCP.name(), pushSetupAlarmMapped.getSendDestinationAndMethod()
                .getTransportService().name());
        assertEquals(MessageType.MANUFACTURER_SPECIFIC.name(), pushSetupAlarmMapped.getSendDestinationAndMethod()
                .getMessage().name());

    }
}
