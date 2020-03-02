/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.InformationObject;

public class AsduFactory {

    public static ASdu ofType(final ASduType asduType) {
        return new Builder(asduType).build();
    }

    private static class Builder {
        private final ASduType asduType;
        private final boolean isSequenceOfElements = false;
        private final CauseOfTransmission causeOfTransmission = CauseOfTransmission.SPONTANEOUS;
        private final boolean test = false;
        private final boolean negativeConfirm = false;
        private final int originatorAddress = 0;
        private final int commonAddress = 1;
        private final InformationObject[] informationObjects = new InformationObject[] {};

        public Builder(final ASduType asduType) {
            this.asduType = asduType;
        }

        public ASdu build() {
            return new ASdu(this.asduType, this.isSequenceOfElements, this.causeOfTransmission, this.test,
                    this.negativeConfirm, this.originatorAddress, this.commonAddress, this.informationObjects);
        }
    }

}
