/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.glue.steps;

import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.INFORMATION_ELEMENT_VALUE;
import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.INFORMATION_OBJECT_ADDRESS;
import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.INFORMATION_OBJECT_TYPE;
import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.PROFILE;

import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessImageSteps {

  @Autowired private Iec60870MockServer mockServer;

  @When("^I update the information object$")
  public void iUpdateTheInformationObject(final Map<String, String> parameters) {
    final Integer informationObjectAddress =
        Integer.valueOf(parameters.get(INFORMATION_OBJECT_ADDRESS));
    final Iec60870InformationObjectType informationObjectType =
        Iec60870InformationObjectType.fromString(parameters.get(INFORMATION_OBJECT_TYPE));
    this.mockServer
        .getRtuSimulator()
        .updateInformationObject(
            informationObjectAddress,
            informationObjectType,
            this.informationElementvalue(parameters.get(INFORMATION_ELEMENT_VALUE)));
  }

  private Object informationElementvalue(final String value) {
    final String profile = (String) ScenarioContext.current().get(PROFILE);
    if ("light_measurement_device".equals(profile)) {
      return Boolean.valueOf(value);
    }
    return Float.valueOf(value);
  }
}
