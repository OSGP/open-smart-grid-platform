/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduBuilder;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("light_measurement_device")
public class LightMeasurementDeviceAsduFactory implements Iec60870AsduFactory {

    @Value("${general_interrogation_object_addresses}")
    private int[] ioa;

    @Value("${general_interrogation_element_values}")
    private boolean[] iev;

    @Override
    public ASdu createInterrogationCommandResponseAsdu() {
        final InformationObject[] informationObjects = new InformationObject[this.ioa.length];
        for (int index = 0; index < this.ioa.length; index++) {
            informationObjects[index] = new InformationObject(this.ioa[index],
                    this.createInformationElement(this.iev[index]));
        }
        return new Iec60870AsduBuilder().withAsduType(ASduType.M_SP_NA_1)
                .withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
                .withInformationObjects(informationObjects)
                .build();
    }

    private InformationElement[][] createInformationElement(final boolean on) {
        return new InformationElement[][] { { new IeSinglePointWithQuality(on, false, false, false, false) } };
    }

}
