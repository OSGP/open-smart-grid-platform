/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;

public class Iec60870ASduBuilder {

    private TypeId typeId = TypeId.M_ME_NB_1;
    private boolean sequenceOfElements = false;
    private CauseOfTransmission causeOfTransmission = CauseOfTransmission.SPONTANEOUS;
    private boolean test = false;
    private boolean negativeConfirm = false;
    private int originatorAddress = 0;
    private int commonAddress = 1;
    private InformationObject[] informationObjects;

    public Iec60870ASduBuilder withTypeId(final TypeId typeId) {
        this.typeId = typeId;
        return this;
    }

    public Iec60870ASduBuilder withSequenceOfElements(final boolean sequenceOfElements) {
        this.sequenceOfElements = sequenceOfElements;
        return this;
    }

    public Iec60870ASduBuilder withCauseOfTransmission(final CauseOfTransmission causeOfTransmission) {
        this.causeOfTransmission = causeOfTransmission;
        return this;
    }

    public Iec60870ASduBuilder withTest(final boolean test) {
        this.test = test;
        return this;
    }

    public Iec60870ASduBuilder withNegativeConfirm(final boolean negativeConfirm) {
        this.negativeConfirm = negativeConfirm;
        return this;
    }

    public Iec60870ASduBuilder withOriginatorAddress(final int originatorAddress) {
        this.originatorAddress = originatorAddress;
        return this;
    }

    public Iec60870ASduBuilder withCommonAddress(final int commonAddress) {
        this.commonAddress = commonAddress;
        return this;
    }

    public Iec60870ASduBuilder withInformationObjects(final InformationObject[] informationObjects) {
        this.informationObjects = informationObjects.clone();
        return this;
    }

    public ASdu build() {
        return new ASdu(this.typeId, this.sequenceOfElements, this.causeOfTransmission, this.test, this.negativeConfirm,
                this.originatorAddress, this.commonAddress, this.informationObjects);
    }
}
