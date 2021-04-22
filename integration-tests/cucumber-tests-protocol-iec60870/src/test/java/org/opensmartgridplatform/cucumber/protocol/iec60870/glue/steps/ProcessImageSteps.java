/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.glue.steps;

import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.PROFILE;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys;
import org.opensmartgridplatform.cucumber.protocol.iec60870.domain.Iec60870InformationObject;
import org.opensmartgridplatform.cucumber.protocol.iec60870.domain.Iec60870ProcessImage;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessImageSteps {

  @Autowired private Iec60870MockServer mockServer;

  @DataTableType
  public Iec60870ProcessImage processImageTable(final DataTable table) {
    final Map<Integer, Iec60870InformationObject> informationObjects =
        table.asMaps().stream()
            .map(this::informationObjectEntry)
            .collect(Collectors.toMap(Iec60870InformationObject::getAddress, Function.identity()));
    return new Iec60870ProcessImage(informationObjects);
  }

  @DataTableType
  public Iec60870InformationObject informationObjectEntry(final Map<String, String> entry) {
    return new Iec60870InformationObject(
        informationObjectAddress(entry),
        informationObjectType(entry),
        informationElementValue(entry));
  }

  @Given("a process image on the IEC60870 server")
  public void givenAProcessImage(final Iec60870ProcessImage processImage) {

    final Consumer<Iec60870InformationObject> setInformationObject =
        informationObject ->
            this.mockServer
                .getRtuSimulator()
                .initInformationObject(
                    informationObject.getAddress(),
                    informationObject.getType(),
                    informationObject.getValue());

    processImage.getInformationObjects().values().forEach(setInformationObject);
  }

  @When("the process image on the IEC60870 server changes")
  public void whenTheProcessImageOnTheIec60870ServerChanges(
      final Iec60870InformationObject informationObject) {

    this.mockServer
        .getRtuSimulator()
        .updateInformationObject(
            informationObject.getAddress(),
            informationObject.getType(),
            informationObject.getValue());
  }

  private static int informationObjectAddress(final Map<String, String> entry) {
    return ReadSettingsHelper.getInteger(entry, ProtocolIec60870Keys.INFORMATION_OBJECT_ADDRESS);
  }

  private static Iec60870InformationObjectType informationObjectType(
      final Map<String, String> entry) {

    return ReadSettingsHelper.getEnum(
        entry, ProtocolIec60870Keys.INFORMATION_OBJECT_TYPE, Iec60870InformationObjectType.class);
  }

  private static Object informationElementValue(final Map<String, String> entry) {
    return informationElementValue(entry.get(ProtocolIec60870Keys.INFORMATION_ELEMENT_VALUE));
  }

  private static Object informationElementValue(final String value) {
    final String profile = (String) ScenarioContext.current().get(PROFILE);
    if ("light_measurement_device".equals(profile)) {
      return Boolean.valueOf(value);
    }
    return Float.valueOf(value);
  }
}