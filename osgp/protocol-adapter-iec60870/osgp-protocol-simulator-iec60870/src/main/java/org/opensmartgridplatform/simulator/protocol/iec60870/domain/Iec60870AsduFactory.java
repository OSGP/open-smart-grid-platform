/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import java.util.Map;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;

public interface Iec60870AsduFactory {

    default void initialize() {

    }

    default IeQualifierOfInterrogation defaultIeQualifierOfInterrogation() {
        final int stationInterrogation = 20;
        return new IeQualifierOfInterrogation(stationInterrogation);
    }

    default ASdu createInterrogationCommandAsdu() {
        return new Iec60870AsduBuilder().withAsduType(ASduType.C_IC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_CON)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation() } }) })
                .build();
    }

    ASdu createInterrogationCommandResponseAsdu();

    default ASdu createActivationTerminationResponseAsdu() {
        return new Iec60870AsduBuilder().withAsduType(ASduType.C_IC_NA_1)
                .withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_TERMINATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation() } }) })
                .build();
    }

    default InformationObject[] processImageToArray(final Map<Integer, InformationElement[][]> map) {
        final Integer[] keys = map.keySet().toArray(new Integer[map.size()]);
        final InformationObject[] informationObjects = new InformationObject[map.size()];
        for (int index = 0; index < map.size(); index++) {
            informationObjects[index] = new InformationObject(keys[index], map.get(keys[index]));
        }
        return informationObjects;
    }

}
