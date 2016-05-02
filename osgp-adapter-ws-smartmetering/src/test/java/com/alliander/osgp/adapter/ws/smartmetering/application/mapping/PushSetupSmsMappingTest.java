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

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupSmsMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final String HOST = "host";
    private static final BigInteger PORT = BigInteger.TEN;
    private static final String DESTINATION = "host:10";
    private static final TransportServiceType TRANSPORTSERVICETYPE = TransportServiceType.TCP;
    private static final MessageType MESSAGETYPE = MessageType.MANUFACTURER_SPECIFIC;

    /**
     * Tests if a PushSetupSms object can be mapped successfully.
     */
    @Test
    public void testPushSetupSmsMapping() {

        // build test data
        final PushSetupSms pushSetupSmsOriginal = new PushSetupSms();
        pushSetupSmsOriginal.setHost(HOST);
        pushSetupSmsOriginal.setPort(PORT);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms pushSetupSmsMapped = this.configurationMapper
                .map(pushSetupSmsOriginal, com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms.class);

        // check mapping
        assertNotNull(pushSetupSmsMapped);
        assertNotNull(pushSetupSmsMapped.getSendDestinationAndMethod());
        assertNotNull(pushSetupSmsMapped.getSendDestinationAndMethod().getDestination());
        assertNotNull(pushSetupSmsMapped.getSendDestinationAndMethod().getTransportService());
        assertNotNull(pushSetupSmsMapped.getSendDestinationAndMethod().getMessage());

        // Only a SendDestinationAndMethod is mapped:
        assertNull(pushSetupSmsMapped.getLogicalName());
        assertNull(pushSetupSmsMapped.getCommunicationWindow());
        assertNull(pushSetupSmsMapped.getNumberOfRetries());
        assertNull(pushSetupSmsMapped.getPushObjectList());
        assertNull(pushSetupSmsMapped.getRandomisationStartInterval());
        assertNull(pushSetupSmsMapped.getRepetitionDelay());

        // port and host are combined into destination. The converter sets
        // default values for the other two variables of a
        // SendDestinationAndMethod.
        assertEquals(DESTINATION, pushSetupSmsMapped.getSendDestinationAndMethod().getDestination());
        assertEquals(TRANSPORTSERVICETYPE.name(), pushSetupSmsMapped.getSendDestinationAndMethod()
                .getTransportService().name());
        assertEquals(MESSAGETYPE.name(), pushSetupSmsMapped.getSendDestinationAndMethod().getMessage().name());

    }

}
