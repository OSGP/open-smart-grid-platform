/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.glue.hooks;

import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.PROFILE;

import io.cucumber.java.Before;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.protocol.iec60870.database.Iec60870Database;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.iec60870.Iec60870ProfileType;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation.DefaultControlledStationAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.lightmeasurementdevice.LightMeasurementDeviceAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandAsduHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class ScenarioHooks {

  @Autowired private Iec60870Database database;

  @Autowired private Iec60870MockServer mockServer;

  @Before(order = 1000)
  public void beforeScenario() {
    this.database.prepareForScenario();
  }

  @Before("@Iec60870MockServerLightMeasurement")
  public void initMockServerLightMeasurement() {
    ScenarioContext.current().put(PROFILE, Iec60870ProfileType.LIGHT_MEASUREMENT_DEVICE);
    final Iec60870AsduFactory factory = new LightMeasurementDeviceAsduFactory();
    factory.setIec60870Server(this.mockServer.getRtuSimulator());
    this.mockServer.addIec60870ASduHandler(
        ASduType.C_IC_NA_1, new Iec60870InterrogationCommandAsduHandler(factory));
  }

  @Before("@Iec60870MockServerDefaultControlledStation")
  public void initMockServerDefaultControlledStation() {
    ScenarioContext.current().put(PROFILE, Iec60870ProfileType.DEFAULT_CONTROLLED_STATION);
    final Iec60870AsduFactory factory = new DefaultControlledStationAsduFactory();
    factory.setIec60870Server(this.mockServer.getRtuSimulator());
    this.mockServer.addIec60870ASduHandler(
        ASduType.C_IC_NA_1, new Iec60870InterrogationCommandAsduHandler(factory));
  }
}
