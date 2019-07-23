/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;

public class AsduFactory {

    public static ASdu ofType(final TypeId typeId) {
        return new Builder(typeId).build();
    }

    private static class Builder {
        private TypeId typeId;
        private boolean isSequenceOfElements = false;
        private CauseOfTransmission causeOfTransmission = CauseOfTransmission.SPONTANEOUS;
        private boolean test = false;
        private boolean negativeConfirm = false;
        private int originatorAddress = 0;
        private int commonAddress = 1;
        private InformationObject[] informationObjects = new InformationObject[] {};

        public Builder(final TypeId typeId) {
            this.typeId = typeId;
        }

        public ASdu build() {
            return new ASdu(this.typeId, this.isSequenceOfElements, this.causeOfTransmission, this.test,
                    this.negativeConfirm, this.originatorAddress, this.commonAddress, this.informationObjects);
        }
    }

}
