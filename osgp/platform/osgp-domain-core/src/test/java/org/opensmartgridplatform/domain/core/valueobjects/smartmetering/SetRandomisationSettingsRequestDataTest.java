package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class SetRandomisationSettingsRequestDataTest {

    @Test
    public void testValidRequestData() {

        try {
            new SetRandomisationSettingsRequestData(SetRandomisationSettingsRequestData.ONE,
                    SetRandomisationSettingsRequestData.ONE, SetRandomisationSettingsRequestData.ONE,
                    SetRandomisationSettingsRequestData.ONE).validate();
        } catch (FunctionalException e) {
            fail();
        }
    }

    @Test(expected = FunctionalException.class)
    public void testInvalidDirectAttach() throws FunctionalException {
        new SetRandomisationSettingsRequestData(SetRandomisationSettingsRequestData.ZERO - 1,
                SetRandomisationSettingsRequestData.ONE, SetRandomisationSettingsRequestData.ONE,
                SetRandomisationSettingsRequestData.ONE).validate();
    }

    @Test(expected = FunctionalException.class)
    public void testInvalidRandomisationStartWindow() throws FunctionalException {
        new SetRandomisationSettingsRequestData(SetRandomisationSettingsRequestData.ZERO,
                SetRandomisationSettingsRequestData.MAX_VALUE_RANDOMIZATION_START_WINDOW + 1,
                SetRandomisationSettingsRequestData.ONE, SetRandomisationSettingsRequestData.ONE).validate();
    }

    @Test(expected = FunctionalException.class)
    public void testInvalidMultiplicationFactor() throws FunctionalException {
        new SetRandomisationSettingsRequestData(SetRandomisationSettingsRequestData.ZERO,
                SetRandomisationSettingsRequestData.MAX_VALUE_RANDOMIZATION_START_WINDOW,
                SetRandomisationSettingsRequestData.MAX_VALUE_MULTIPLICATION_FACTOR + 1,
                SetRandomisationSettingsRequestData.ONE).validate();
    }

    @Test(expected = FunctionalException.class)
    public void testInvalidNumberOfRetries() throws FunctionalException {
        new SetRandomisationSettingsRequestData(SetRandomisationSettingsRequestData.ZERO,
                SetRandomisationSettingsRequestData.MAX_VALUE_RANDOMIZATION_START_WINDOW,
                SetRandomisationSettingsRequestData.MAX_VALUE_MULTIPLICATION_FACTOR,
                SetRandomisationSettingsRequestData.MAX_VALUE_NUMBER_OF_RETRIES + 1).validate();
    }
}
