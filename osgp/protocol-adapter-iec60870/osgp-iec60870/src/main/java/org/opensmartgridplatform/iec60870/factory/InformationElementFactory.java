/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870.factory;

import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationElement;
import org.opensmartgridplatform.iec60870.exceptions.InformationObjectTypeNotSupported;
import org.springframework.stereotype.Component;

@Component
public class InformationElementFactory {

    public InformationElement[][] createInformationElements(final String informationObjectType, final Object value) {
        if ("IeShortFloat".equals(informationObjectType)) {
            return new InformationElement[][] {
                    { new IeShortFloat((Float) value), new IeQuality(false, false, false, false, false) } };
        }
        if ("IeSinglePointWithQuality".equals(informationObjectType)) {
            return new InformationElement[][] {
                    { new IeSinglePointWithQuality((Boolean) value, false, false, false, false) } };
        }
        if ("IeQualifierOfInterrogation".equals(informationObjectType)) {
            return new InformationElement[][] { { new IeQualifierOfInterrogation((Integer) value) } };
        }
        throw new InformationObjectTypeNotSupported(informationObjectType + " is not supported yet");
    }
}
