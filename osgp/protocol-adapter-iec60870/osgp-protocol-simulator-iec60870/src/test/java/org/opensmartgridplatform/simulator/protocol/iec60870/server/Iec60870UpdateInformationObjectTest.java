/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationElement;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.Iec60870ServerEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("light_measurement_device")
class Iec60870UpdateInformationObjectTest {

    @Autowired
    private Iec60870Server iec60870Server;

    @Mock
    private Connection connection;

    private static boolean ON = true;
    private static final IeSinglePointWithQuality EXPECTED = new IeSinglePointWithQuality(ON, false, false, false,
            false);

    /**
     * informationObjectAddress 42 is in
     * application-light_measurement_device.properties with value false, we will
     * test updating that address
     */
    @Test
    void testUpdateExistingInformationObject() {

        final int informationObjectAddress = 42;

        this.iec60870Server.updateInformationObject(informationObjectAddress, "IeSinglePointWithQuality", ON);

        final InformationElement[][] actual = this.iec60870Server.getProcessImage().get(informationObjectAddress);
        assertThat(actual[0][0]).usingRecursiveComparison().isEqualTo(EXPECTED);
    }

    /**
     * informationObjectAddress 2 is not in
     * application-light_measurement_device.properties, we will test updating
     * that address
     */
    @Test
    void testAddInformationObject() {

        final int informationObjectAddress = 2;

        this.iec60870Server.updateInformationObject(informationObjectAddress, "IeSinglePointWithQuality", ON);

        final InformationElement[][] actual = this.iec60870Server.getProcessImage().get(informationObjectAddress);
        assertThat(actual[0][0]).usingRecursiveComparison().isEqualTo(EXPECTED);
    }

    @Test
    void testSendEvent() throws IOException {

        this.registerConnection();

        this.iec60870Server.updateInformationObject(1, "IeSinglePointWithQuality", ON);

        // check if an event was sent
        verify(this.connection)
                .send(argThat(new AsduTypeArgumentMatcher(ASduType.M_SP_TB_1, CauseOfTransmission.SPONTANEOUS)));

    }

    private void registerConnection() throws IOException {
        final Iec60870ServerEventListener eventListener = this.iec60870Server.getIec60870ServerEventListener();
        eventListener.connectionIndication(this.connection);
    }

}
