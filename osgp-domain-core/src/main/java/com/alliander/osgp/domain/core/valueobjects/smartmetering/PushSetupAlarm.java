/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class PushSetupAlarm extends AbstractPushSetup implements Serializable, ActionValueObject {

    private static final long serialVersionUID = -3541154908239512383L;

    public static class Builder extends AbstractPushSetup.AbstractBuilder {

        @Override
        public PushSetupAlarm build() {
            return new PushSetupAlarm(this.logicalName, this.pushObjectList, this.sendDestinationAndMethod,
                    this.communicationWindow, this.randomisationStartInterval, this.numberOfRetries,
                    this.repetitionDelay);
        }
    }

    public PushSetupAlarm(final CosemObisCode logicalName, final List<CosemObjectDefinition> pushObjectList,
            final SendDestinationAndMethod sendDestinationAndMethod, final List<WindowElement> communicationWindow,
            final Integer randomisationStartInterval, final Integer numberOfRetries, final Integer repetitionDelay) {

        super(logicalName, pushObjectList, sendDestinationAndMethod, communicationWindow, randomisationStartInterval,
                numberOfRetries, repetitionDelay);
    }

    @Override
    public void validate() throws FunctionalException {
        // TODO Auto-generated method stub

    }
}
