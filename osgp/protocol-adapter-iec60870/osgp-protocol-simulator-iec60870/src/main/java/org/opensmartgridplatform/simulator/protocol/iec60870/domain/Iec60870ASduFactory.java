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
import org.openmuc.j60870.IeQualifierOfInterrogation;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeScaledValue;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.springframework.stereotype.Component;

@Component
public class Iec60870ASduFactory {

    private IeQualifierOfInterrogation defaultIeQualifierOfInterrogation = new IeQualifierOfInterrogation(20);
    private IeQuality defaultIeQuality = new IeQuality(true, true, true, true, true);

    public ASdu createInterrogationCommandASdu() {
        return new Iec60870ASduBuilder().withTypeId(TypeId.C_IC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation } }) })
                .build();
    }

    public ASdu createInterrogationCommandResponseASdu() {
        return new Iec60870ASduBuilder().withTypeId(TypeId.M_ME_NB_1).withSequenceOfElements(true)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] { new InformationObject(1,
                        new InformationElement[][] { { new IeScaledValue(-32768), this.defaultIeQuality },
                                { new IeScaledValue(10), this.defaultIeQuality },
                                { new IeScaledValue(-5), this.defaultIeQuality } }) })
                .build();
    }

    public ASdu createSingleCommandASdu() {
        return new Iec60870ASduBuilder().withTypeId(TypeId.C_SC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.SPONTANEOUS)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation } }) })
                .build();
    }

}
