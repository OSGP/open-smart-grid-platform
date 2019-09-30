/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetRandomisationSettingsRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = -381163520662276869L;

    private int directAttach;
    private int randomisationStartWindow;
    private int multiplicationFactor;
    private int numberOfRetries;

    public SetRandomisationSettingsRequestData(final int directAttach, final int randomisationStartWindow,
            final int multiplicationFactor, final int numberOfRetries) {
        this.directAttach = directAttach;
        this.randomisationStartWindow = randomisationStartWindow;
        this.multiplicationFactor = multiplicationFactor;
        this.numberOfRetries = numberOfRetries;
    }

    @Override
    public void validate() throws FunctionalException {
        // No validation needed
    }

    public int getDirectAttach() {
        return directAttach;
    }

    public int getRandomisationStartWindow() {
        return randomisationStartWindow;
    }

    public int getMultiplicationFactor() {
        return multiplicationFactor;
    }

    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.SET_RANDOMISATION_SETTINGS;
    }
}
