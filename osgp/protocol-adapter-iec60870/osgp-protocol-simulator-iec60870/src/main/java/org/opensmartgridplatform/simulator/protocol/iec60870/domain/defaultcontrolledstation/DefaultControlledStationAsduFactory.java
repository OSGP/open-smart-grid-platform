// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.factory.InformationElementFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduBuilder;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default_controlled_station")
public class DefaultControlledStationAsduFactory implements Iec60870AsduFactory {

  @Value("${general_interrogation_object_addresses}")
  private final int[] ioa = new int[0];

  @Value("${general_interrogation_element_values}")
  private final float[] iev = new float[0];

  @Autowired private Iec60870Server iec60870Server;

  private final InformationElementFactory informationElementFactory =
      new InformationElementFactory();

  @PostConstruct
  @Override
  public void initialize() {
    final Map<Integer, InformationElement[][]> processImage = new HashMap<>();
    for (int index = 0; index < this.ioa.length; index++) {
      processImage.put(
          this.ioa[index],
          this.informationElementFactory.createInformationElements(
              Iec60870InformationObjectType.SHORT_FLOAT, this.iev[index]));
    }
    this.iec60870Server.setProcessImage(processImage);
  }

  @Override
  public ASdu createInterrogationCommandResponseAsdu() {

    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.M_ME_NC_1)
        .withSequenceOfElements(false)
        .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
        .withInformationObjects(this.processImageToArray(this.iec60870Server.getProcessImage()))
        .build();
  }

  public ASdu createSingleCommandAsdu() {
    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.C_SC_NA_1)
        .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
        .withInformationObjects(
            new InformationObject[] {
              new InformationObject(
                  0, new InformationElement[][] {{this.defaultIeQualifierOfInterrogation()}})
            })
        .build();
  }

  public ASdu createShortFloatingPointMeasurementAsdu() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    final long timestamp = now.toInstant().toEpochMilli();
    final float hour = now.getHour();
    final float minute = now.getMinute();

    final InformationObject[] informationObjects = new InformationObject[this.ioa.length];
    for (int index = 0; index < this.ioa.length; index++) {
      final float value = (index == 0 ? hour : minute);
      informationObjects[index] =
          new InformationObject(
              this.ioa[index], this.createInformationElementWithTimetag(value, timestamp));
    }

    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.M_ME_TF_1)
        .withSequenceOfElements(false)
        .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
        .withInformationObjects(informationObjects)
        .build();
  }

  private InformationElement[][] createInformationElementWithTimetag(
      final float value, final long timestamp) {
    return new InformationElement[][] {
      {
        new IeShortFloat(value),
        new IeQuality(false, false, false, false, false),
        new IeTime56(timestamp)
      }
    };
  }

  @Override
  public void setIec60870Server(final Iec60870Server iec60870Server) {
    this.iec60870Server = iec60870Server;
  }
}
