/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.InformationElement;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.factory.InformationElementFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduBuilder;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("light_measurement_device")
public class LightMeasurementDeviceAsduFactory implements Iec60870AsduFactory {

    @Value("${general_interrogation_object_addresses}")
    private final int[] ioa = new int[0];

    @Value("${general_interrogation_element_values}")
    private final boolean[] iev = new boolean[0];

    @Autowired
    private Iec60870Server iec60870Server;

    private final InformationElementFactory informationElementFactory = new InformationElementFactory();

    @PostConstruct
    @Override
    public void initialize() {
        final Map<Integer, InformationElement[][]> processImage = new HashMap<>();
        for (int index = 0; index < this.ioa.length; index++) {
            processImage.put(this.ioa[index], this.informationElementFactory
                    .createInformationElements("IeSinglePointWithQuality", this.iev[index]));
        }
        this.iec60870Server.setProcessImage(processImage);
    }

    @Override
    public ASdu createInterrogationCommandResponseAsdu() {
        return new Iec60870AsduBuilder().withAsduType(ASduType.M_SP_NA_1)
                .withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
                .withInformationObjects(this.processImageToArray(this.iec60870Server.getProcessImage()))
                .build();
    }

    @Override
    public void setIec60870Server(final Iec60870Server iec60870Server) {
        this.iec60870Server = iec60870Server;
    }

}
