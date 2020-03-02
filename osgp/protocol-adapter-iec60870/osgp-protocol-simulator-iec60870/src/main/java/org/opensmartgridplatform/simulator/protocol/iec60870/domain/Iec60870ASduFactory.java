/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.springframework.stereotype.Component;

@Component
public class Iec60870ASduFactory {

    private static final int IOA_9127 = 9127;
    private static final int IOA_9128 = 9128;
    private static final float VALUE_9127 = 10.0f;
    private static final float VALUE_9128 = 20.5f;

    // @formatter:off
    private static final IeQualifierOfInterrogation DEFAULT_IE_QUALIFIER_OF_INTERROGATION =
            new IeQualifierOfInterrogation(20);

    public ASdu createInterrogationCommandAsdu() {
        return new Iec60870ASduBuilder().withTypeId(ASduType.C_IC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { DEFAULT_IE_QUALIFIER_OF_INTERROGATION } }) })
                .build();
    }

    public ASdu createInterrogationCommandResponseAsdu() {
        final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        return this.createInterrogationCommandResponseAsdu(timestamp);
    }

    public ASdu createInterrogationCommandResponseAsdu(final long timestamp) {
        return new Iec60870ASduBuilder().withTypeId(ASduType.M_ME_TF_1).withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] {
                        new InformationObject(
                                IOA_9127,
                                new InformationElement[][] { {
                                    new IeShortFloat(VALUE_9127),
                                    new IeQuality(false, false, false, false, false),
                                    new IeTime56(timestamp) } }),
                        new InformationObject(
                                IOA_9128,
                                new InformationElement[][] { {
                                    new IeShortFloat(VALUE_9128),
                                    new IeQuality(false, false, false, false, false),
                                    new IeTime56(timestamp) } })
                })
                .build();
    }

    public ASdu createActivationTerminationResponseAsdu() {
        return new Iec60870ASduBuilder().withTypeId(ASduType.C_IC_NA_1).withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_TERMINATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { DEFAULT_IE_QUALIFIER_OF_INTERROGATION } }) })
                .build();
    }

    public ASdu createSingleCommandAsdu() {
        return new Iec60870ASduBuilder().withTypeId(ASduType.C_SC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { DEFAULT_IE_QUALIFIER_OF_INTERROGATION } }) })
                .build();
    }

    public ASdu createShortFloatingPointMeasurementAsdu() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final long timestamp = now.toInstant().toEpochMilli();
        final float hour = now.getHour();
        final float minute = now.getMinute();

        return new Iec60870ASduBuilder().withTypeId(ASduType.M_ME_TF_1).withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] {
                        new InformationObject(
                                IOA_9127,
                                new InformationElement[][] { {
                                    new IeShortFloat(hour),
                                    new IeQuality(false, false, false, false, false),
                                    new IeTime56(timestamp) } }),
                        new InformationObject(
                                IOA_9128,
                                new InformationElement[][] { {
                                    new IeShortFloat(minute),
                                    new IeQuality(false, false, false, false, false),
                                    new IeTime56(timestamp) } })
                })
                .build();

    }
    // @formatter:on
}
