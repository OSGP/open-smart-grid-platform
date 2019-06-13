/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;

public class Iec60870DeviceSteps {

    @Autowired
    private Iec60870DeviceRepository repositoryMock;

    @Given("an IEC60870 device")
    public void givenIec60870Device() {
        final Iec60870Device device = Iec60870DeviceFactory.createDefaultWith(DEFAULT_DEVICE_IDENTIFICATION);
        when(this.repositoryMock.findByDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)).thenReturn(device);
    }
}
