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
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class SetRandomisationSettingsRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = -381163520662276869L;

    private int directAttach;
    private int randomisationStartWindow;
    private int multiplicationFactor;
    private int numberOfRetries;

    static final int ZERO = 0;
    static final int ONE = 1;
    static final int MAX_VALUE_RANDOMIZATION_START_WINDOW = 65535;
    static final int MAX_VALUE_MULTIPLICATION_FACTOR = 7;
    static final int MAX_VALUE_NUMBER_OF_RETRIES = 31;

    SetRandomisationSettingsRequestData(final int directAttach, final int randomisationStartWindow,
            final int multiplicationFactor, final int numberOfRetries) {
        this.directAttach = directAttach;
        this.randomisationStartWindow = randomisationStartWindow;
        this.multiplicationFactor = multiplicationFactor;
        this.numberOfRetries = numberOfRetries;
    }

    @Override
    public void validate() throws FunctionalException {

        if (directAttach < ZERO || directAttach > ONE) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new Exception("DirectAttach value range failed. (0-1)"));
        }

        if (randomisationStartWindow < ONE || randomisationStartWindow > MAX_VALUE_RANDOMIZATION_START_WINDOW) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new Exception("RandomisationStartWindow value range failed. (1-65535)"));
        }

        if (multiplicationFactor < ONE || multiplicationFactor > MAX_VALUE_MULTIPLICATION_FACTOR) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new Exception("MultiplicationFactor value range failed. (1-7)"));
        }

        if (numberOfRetries < ONE || numberOfRetries > MAX_VALUE_NUMBER_OF_RETRIES) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new Exception("NumberOfRetries value range failed. (1-31)"));
        }
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
