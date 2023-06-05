// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.protocol.iec60870.glue.steps;

import static org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys.PROFILE;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.protocol.iec60870.ProtocolIec60870Keys;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.iec60870.Iec60870InformationObject;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.iec60870.Iec60870ProcessImage;
import org.opensmartgridplatform.iec60870.Iec60870ProfileType;
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
  public void givenAProcessImageOnTheIec60870Server(final Iec60870ProcessImage processImage) {
    this.mockServer.getRtuSimulator().setProcessImage(processImage);
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
    final Iec60870ProfileType profile =
        (Iec60870ProfileType) ScenarioContext.current().get(PROFILE);
    if (Iec60870ProfileType.LIGHT_MEASUREMENT_DEVICE == profile) {
      return Boolean.valueOf(value);
    }
    return Float.valueOf(value);
  }
}
