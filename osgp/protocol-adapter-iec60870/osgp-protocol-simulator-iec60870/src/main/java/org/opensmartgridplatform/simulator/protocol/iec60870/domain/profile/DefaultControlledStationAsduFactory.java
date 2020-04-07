/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduBuilder;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default_controlled_station")
public class DefaultControlledStationAsduFactory implements Iec60870AsduFactory {

    @Value("${general_interrogation_object_addresses}")
    private int[] ioa;

    @Value("${general_interrogation_element_values}")
    private float[] iev;

    @Override
    public ASdu createInterrogationCommandResponseAsdu() {
        final InformationObject[] informationObjects = new InformationObject[this.ioa.length];
        for (int index = 0; index < this.ioa.length; index++) {
            informationObjects[index] = new InformationObject(this.ioa[index],
                    this.createInformationElementWithoutTimetag(this.iev[index]));
        }

        return new Iec60870AsduBuilder().withAsduType(ASduType.M_ME_NC_1)
                .withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
                .withInformationObjects(informationObjects)
                .build();
    }

    private InformationElement[][] createInformationElementWithoutTimetag(final float value) {
        return new InformationElement[][] {
                { new IeShortFloat(value), new IeQuality(false, false, false, false, false) } };
    }

    public ASdu createSingleCommandAsdu() {
        return new Iec60870AsduBuilder().withAsduType(ASduType.C_SC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation() } }) })
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
            informationObjects[index] = new InformationObject(this.ioa[index],
                    this.createInformationElementWithTimetag(value, timestamp));
        }

        return new Iec60870AsduBuilder().withAsduType(ASduType.M_ME_TF_1)
                .withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(informationObjects)
                .build();

    }

    private InformationElement[][] createInformationElementWithTimetag(final float value, final long timestamp) {
        return new InformationElement[][] { { new IeShortFloat(value), new IeQuality(false, false, false, false, false),
                new IeTime56(timestamp) } };
    }

    // TODO: remove these setters when the cucumber mock server can read these
    // values from a property file
    public void setIoa(final int[] ioa) {
        this.ioa = ioa;
    }

    public void setIev(final float[] iev) {
        this.iev = iev;
    }
}
