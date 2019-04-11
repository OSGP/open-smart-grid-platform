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
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.IeQualifierOfInterrogation;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
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

    public ASdu createInterrogationCommandASdu() {
        return new Iec60870ASduBuilder().withTypeId(TypeId.C_IC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { DEFAULT_IE_QUALIFIER_OF_INTERROGATION } }) })
                .build();
    }

    public ASdu createInterrogationCommandResponseASdu() {
        final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        return this.createInterrogationCommandResponseASdu(timestamp);
    }

    public ASdu createInterrogationCommandResponseASdu(final long timestamp) {
        return new Iec60870ASduBuilder().withTypeId(TypeId.M_ME_TF_1).withSequenceOfElements(false)
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

    public ASdu createActivationTerminationResponseASdu() {
        return new Iec60870ASduBuilder().withTypeId(TypeId.C_IC_NA_1).withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_TERMINATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { DEFAULT_IE_QUALIFIER_OF_INTERROGATION } }) })
                .build();
    }

    public ASdu createSingleCommandASdu() {
        return new Iec60870ASduBuilder().withTypeId(TypeId.C_SC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { DEFAULT_IE_QUALIFIER_OF_INTERROGATION } }) })
                .build();
    }
    // @formatter:on
}
