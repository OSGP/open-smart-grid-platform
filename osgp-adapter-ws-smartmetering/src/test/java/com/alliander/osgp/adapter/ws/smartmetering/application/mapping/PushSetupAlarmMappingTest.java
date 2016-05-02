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

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupAlarmMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final String HOST = "host";
    private static final BigInteger PORT = BigInteger.TEN;
    private static final String DESTINATION = "host:10";
    private static final TransportServiceType TRANSPORTSERVICETYPE = TransportServiceType.TCP;
    private static final MessageType MESSAGETYPE = MessageType.MANUFACTURER_SPECIFIC;

    /**
     * Tests if mapping a PushSetupAlarm object succeeds.
     */
    @Test
    public void testPushSetupAlarmMapping() {

        // build test data
        final PushSetupAlarm pushSetupAlarmOriginal = new PushSetupAlarm();
        pushSetupAlarmOriginal.setHost(HOST);
        pushSetupAlarmOriginal.setPort(PORT);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm pushSetupAlarmMapped = this.configurationMapper
                .map(pushSetupAlarmOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);

        // check mapping
        assertNotNull(pushSetupAlarmMapped);
        assertNotNull(pushSetupAlarmMapped.getSendDestinationAndMethod());
        assertNotNull(pushSetupAlarmMapped.getSendDestinationAndMethod().getDestination());
        assertNotNull(pushSetupAlarmMapped.getSendDestinationAndMethod().getTransportService());
        assertNotNull(pushSetupAlarmMapped.getSendDestinationAndMethod().getMessage());

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
        assertEquals(DESTINATION, pushSetupAlarmMapped.getSendDestinationAndMethod().getDestination());
        assertEquals(TRANSPORTSERVICETYPE.name(), pushSetupAlarmMapped.getSendDestinationAndMethod()
                .getTransportService().name());
        assertEquals(MESSAGETYPE.name(), pushSetupAlarmMapped.getSendDestinationAndMethod().getMessage().name());

    }
}
