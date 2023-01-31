/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.lightmeasurementdevice;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.factory.InformationElementFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduBuilder;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("light_measurement_device")
public class LightMeasurementDeviceAsduFactory implements Iec60870AsduFactory {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LightMeasurementDeviceAsduFactory.class);

  private Random random = new Random();

  @Value("${general_interrogation_object_addresses}")
  private final int[] ioa = new int[0];

  @Value("${general_interrogation_element_values}")
  private final boolean[] iev = new boolean[0];

  private final String timeZoneString;

  @Autowired private Iec60870Server iec60870Server;

  private final InformationElementFactory informationElementFactory =
      new InformationElementFactory();

  public LightMeasurementDeviceAsduFactory(
      @Value("${job.asdu.generator.time.zone}") final String timeZoneString) {
    this.timeZoneString = timeZoneString;
  }

  @PostConstruct
  @Override
  public void initialize() {
    final Map<Integer, InformationElement[][]> processImage = new HashMap<>();
    for (int index = 0; index < this.ioa.length; index++) {
      processImage.put(
          this.ioa[index],
          this.informationElementFactory.createInformationElements(
              Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY,
              this.iev[index]));
    }
    this.iec60870Server.setProcessImage(processImage);
  }

  @Override
  public ASdu createInterrogationCommandResponseAsdu() {
    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.M_SP_NA_1)
        .withSequenceOfElements(false)
        .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
        .withInformationObjects(this.processImageToArray(this.iec60870Server.getProcessImage()))
        .build();
  }

  @Override
  public void setIec60870Server(final Iec60870Server iec60870Server) {
    this.iec60870Server = iec60870Server;
  }

  public ASdu createLightMeasurementEvent() {

    final long timestamp = System.currentTimeMillis();
    final InformationObject[] informationObjects = new InformationObject[1];

    final int index = this.random.nextInt(this.ioa.length);
    this.switchValue(index);
    final String eventValue = this.iev[index] ? "DARK" : "LIGHT";

    LOGGER.info(
        "Creating Light Measurement event for IOA {} with value {} ({})",
        this.ioa[index],
        this.iev[index],
        eventValue);

    informationObjects[0] =
        new InformationObject(
            this.ioa[index], this.createInformationElementWithTimetag(this.iev[index], timestamp));

    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.M_SP_TB_1)
        .withSequenceOfElements(false)
        .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
        .withInformationObjects(informationObjects)
        .build();
  }

  private void switchValue(final int index) {
    this.iev[index] = !this.iev[index];

    final Map<Integer, InformationElement[][]> processImage = this.iec60870Server.getProcessImage();
    processImage.put(
        this.ioa[index],
        this.informationElementFactory.createInformationElements(
            Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY, this.iev[index]));
    this.iec60870Server.setProcessImage(processImage);
  }

  private InformationElement[][] createInformationElementWithTimetag(
      final boolean value, final long timestamp) {

    final TimeZone timeZone = TimeZone.getTimeZone(this.timeZoneString);
    final boolean invalid = false;

    return new InformationElement[][] {
      {
        new IeSinglePointWithQuality(value, false, false, false, false),
        new IeTime56(timestamp, timeZone, invalid)
      }
    };
  }
}
