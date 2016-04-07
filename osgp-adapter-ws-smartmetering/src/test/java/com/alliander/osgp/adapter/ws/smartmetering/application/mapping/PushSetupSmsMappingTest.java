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

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupSmsMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // This mapping needs it's converter.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new PushSetupSmsConverter());
    }

    // Test if mapping a PushSetupSms object succeeds.
    @Test
    public void testPushSetupSmsMapping() {
        // build test data
        final String host = "host";
        final BigInteger port = BigInteger.TEN;
        final PushSetupSms pushSetupSmsOriginal = new PushSetupSms();
        pushSetupSmsOriginal.setHost(host);
        pushSetupSmsOriginal.setPort(port);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms pushSetupSmsMapped = this.mapperFactory
                .getMapperFacade().map(pushSetupSmsOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms.class);

        // check mapping
        assertNotNull(pushSetupSmsMapped);
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
        assertEquals(host + ":" + port, pushSetupSmsMapped.getSendDestinationAndMethod().getDestination());
        assertEquals(TransportServiceType.TCP.name(), pushSetupSmsMapped.getSendDestinationAndMethod()
                .getTransportService().name());
        assertEquals(MessageType.MANUFACTURER_SPECIFIC.name(), pushSetupSmsMapped.getSendDestinationAndMethod()
                .getMessage().name());

    }

}
