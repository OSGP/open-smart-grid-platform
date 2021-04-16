/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ControlledStationSteps {
  @Autowired private ConnectionSteps connectionSteps;

  @Autowired private Connection connection;

  @Given("a process image on the controlled station")
  public void givenAProcessImageOnTheControlledStation(final DataTable processImageTable)
      throws Throwable {

    final ProcessImage processImage = ProcessImage.fromDataTable(processImageTable);

    doAnswer(
            invocation ->
                this.sendInterrogationResponse(
                    invocation.getArgument(0), invocation.getArgument(2), processImage))
        .when(this.connection)
        .interrogation(
            any(Integer.class),
            eq(CauseOfTransmission.ACTIVATION),
            any(IeQualifierOfInterrogation.class));
  }

  private Object sendInterrogationResponse(
      final int commonAddress,
      final IeQualifierOfInterrogation qualifier,
      final ProcessImage processImage) {

    final ConnectionEventListener listener = this.connectionSteps.getConnectionEventListener();
    listener.newASdu(this.interrogationActivationConfirmationAsdu(commonAddress, qualifier));
    for (final ASdu asdu : processImage.toInterrogationAsdus(commonAddress, qualifier)) {
      listener.newASdu(asdu);
    }
    listener.newASdu(this.interrogationActivationTerminationAsdu(commonAddress, qualifier));
    return null;
  }

  private ASdu interrogationActivationConfirmationAsdu(
      final int commonAddress, final IeQualifierOfInterrogation qualifier) {
    return this.interrogationAsdu(commonAddress, CauseOfTransmission.ACTIVATION_CON, qualifier);
  }

  private ASdu interrogationActivationTerminationAsdu(
      final int commonAddress, final IeQualifierOfInterrogation qualifier) {
    return this.interrogationAsdu(
        commonAddress, CauseOfTransmission.ACTIVATION_TERMINATION, qualifier);
  }

  private ASdu interrogationAsdu(
      final int commonAddress,
      final CauseOfTransmission cot,
      final IeQualifierOfInterrogation qualifier) {
    return new ASdu(
        ASduType.C_IC_NA_1,
        false,
        cot,
        false,
        false,
        0,
        commonAddress,
        new InformationObject(0, qualifier));
  }

  private static class ProcessImage {
    private final List<InformationObject> image;

    private ProcessImage(final List<InformationObject> image) {
      this.image = image;
    }

    public static ProcessImage fromDataTable(final DataTable processImageTable) {

      final List<InformationObject> informationObjects =
          processImageTable.asMaps().stream()
              .map(ProcessImage::informationObject)
              .collect(Collectors.toList());
      return new ProcessImage(informationObjects);
    }

    public List<ASdu> toInterrogationAsdus(
        final int commonAddress, final IeQualifierOfInterrogation qualifier) {
      final CauseOfTransmission cot = CauseOfTransmission.causeFor(qualifier.getValue());
      final List<ASdu> asdus = new ArrayList<>();
      asdus.add(
          new ASdu(
              ASduType.M_SP_NA_1,
              true,
              cot,
              false,
              false,
              0,
              commonAddress,
              this.image.toArray(new InformationObject[0])));
      return asdus;
    }

    public static InformationObject informationObject(final Map<String, String> map) {
      final int informationObjectAddress =
          Integer.parseInt(map.getOrDefault("information_object_address", "0"));
      final String informationElementValue = map.get("information_element_value");
      final boolean on = "ON".equalsIgnoreCase(informationElementValue);
      return new InformationObject(
          informationObjectAddress, new IeSinglePointWithQuality(on, false, false, false, false));
    }
  }
}
