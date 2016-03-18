/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.List;

public class PushSetupAlarm extends AbstractPushSetup {

    private static final long serialVersionUID = -3541154908239512383L;

    public static class Builder extends AbstractPushSetup.AbstractBuilder {

        @Override
        public PushSetupAlarm build() {
            return new PushSetupAlarm(this.logicalName, this.pushObjectList, this.sendDestinationAndMethod,
                    this.communicationWindow, this.randomisationStartInterval, this.numberOfRetries,
                    this.repetitionDelay);
        }
    }

    private PushSetupAlarm(final CosemObisCode logicalName, final List<CosemObjectDefinition> pushObjectList,
            final SendDestinationAndMethod sendDestinationAndMethod, final List<WindowElement> communicationWindow,
            final Integer randomisationStartInterval, final Integer numberOfRetries, final Integer repetitionDelay) {
        super(logicalName, pushObjectList, sendDestinationAndMethod, communicationWindow, randomisationStartInterval,
                numberOfRetries, repetitionDelay);
    }
}
