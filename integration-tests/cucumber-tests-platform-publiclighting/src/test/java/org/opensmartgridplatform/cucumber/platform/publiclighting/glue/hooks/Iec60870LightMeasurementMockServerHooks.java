/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.hooks;

import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile.LightMeasurementDeviceAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandAsduHandler;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.Before;

public class Iec60870LightMeasurementMockServerHooks {

    @Autowired
    private Iec60870MockServer iec60870MockServer;

    @Before("@Iec60870LightMeasurementMockServer")
    public void initServer() {
        final Iec60870AsduFactory factory = new LightMeasurementDeviceAsduFactory();
        factory.setIec60870Server(this.iec60870MockServer.getRtuSimulator());
        this.iec60870MockServer.addIec60870ASduHandler(ASduType.C_IC_NA_1,
                new Iec60870InterrogationCommandAsduHandler(factory));
    }
}
