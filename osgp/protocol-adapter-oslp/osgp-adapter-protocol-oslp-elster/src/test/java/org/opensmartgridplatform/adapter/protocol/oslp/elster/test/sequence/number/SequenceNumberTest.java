/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.test.sequence.number;

import org.junit.Assert;
import org.junit.Test;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.DeviceRegistrationService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;

public class SequenceNumberTest {

    private class TestableDeviceRegistrationService extends DeviceRegistrationService {
        public TestableDeviceRegistrationService() {
            this.setSequenceNumberMaximum(65535);
            this.setSequenceNumberWindow(6);
        }
    }

    private TestableDeviceRegistrationService testableDeviceRegistrationService = new TestableDeviceRegistrationService();

    @Test
    public void test1() {
        this.callFunction(1, 1, false);
        this.callFunction(1, 2, false);
        this.callFunction(1, 3, false);
        this.callFunction(1, 4, false);
        this.callFunction(1, 5, false);
        this.callFunction(1, 6, false);
        this.callFunction(1, 7, false);
        this.callFunction(1, 8, false);
        this.callFunction(1, 9, true);
        this.callFunction(1, 10, true);
    }

    @Test
    public void test2() {
        this.callFunction(1, 2, false);
        this.callFunction(1, 1, false);
        this.callFunction(1, 0, false);
        this.callFunction(1, 65535, false);
        this.callFunction(1, 65534, false);
        this.callFunction(1, 65533, false);
        this.callFunction(1, 65532, false);
        this.callFunction(1, 65531, true);
        this.callFunction(1, 65530, true);
    }

    @Test
    public void test3() {
        this.callFunction(65530, 65531, false);
        this.callFunction(65530, 65532, false);
        this.callFunction(65530, 65533, false);
        this.callFunction(65530, 65534, false);
        this.callFunction(65530, 65535, false);
        this.callFunction(65530, 0, false);
        this.callFunction(65530, 1, false);
        this.callFunction(65530, 2, true);
        this.callFunction(65530, 3, true);
    }

    @Test
    public void test4() {
        this.callFunction(65535, 65528, true);
        this.callFunction(65535, 65529, true);
        this.callFunction(65535, 65530, false);
        this.callFunction(65535, 65531, false);
        this.callFunction(65535, 65532, false);
        this.callFunction(65535, 65533, false);
        this.callFunction(65535, 65534, false);
        this.callFunction(65535, 65535, false);
        this.callFunction(65535, 0, false);
        this.callFunction(65535, 1, false);
        this.callFunction(65535, 2, false);
        this.callFunction(65535, 3, false);
        this.callFunction(65535, 4, false);
        this.callFunction(65535, 5, false);
        this.callFunction(65535, 6, false);
        this.callFunction(65535, 7, true);
        this.callFunction(65535, 8, true);
    }

    private void callFunction(final int currentSeqNum, final int newSeqNum, final boolean exceptionExpected) {
        try {
            this.testableDeviceRegistrationService.checkSequenceNumber(currentSeqNum, newSeqNum);
            if (exceptionExpected) {
                Assert.fail("ProtocolAdapterException expected");
            }
        } catch (final ProtocolAdapterException e) {
            if (!exceptionExpected) {
                Assert.fail(e.getMessage());
            }
        }
    }
}
