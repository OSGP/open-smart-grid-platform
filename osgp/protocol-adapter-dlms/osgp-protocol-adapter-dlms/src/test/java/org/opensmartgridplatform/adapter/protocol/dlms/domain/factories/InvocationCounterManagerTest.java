/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceSessionTerminatedAfterReadingInvocationCounterException;

@RunWith(MockitoJUnitRunner.class)
public class InvocationCounterManagerTest {

    private InvocationCounterManager manager;

    @Mock
    private DlmsConnectionFactory connectionFactory;

    private DlmsConnectionManagerStub dlmsConnectionManagerStub;
    private DlmsConnectionStub dlmsConnectionStub;

    @Mock
    private DlmsDeviceRepository deviceRepository;

    @Before
    public void setUp() {
        this.manager = new InvocationCounterManager(this.connectionFactory, this.deviceRepository);
        this.dlmsConnectionStub = new DlmsConnectionStub();
        this.dlmsConnectionManagerStub = new DlmsConnectionManagerStub(this.dlmsConnectionStub);
    }

    @Test
    public void initializesInvocationCounterForDevice() throws Exception {
        final DlmsDevice device = new DlmsDeviceBuilder().withIdentification("id123").build();

        when(this.connectionFactory.getPublicClientConnection(device, null)).thenReturn(this.dlmsConnectionManagerStub);

        final DataObject dataObject = DataObject.newInteger32Data(123);
        this.dlmsConnectionStub.setDefaultReturnValue(dataObject);

        try {
            this.manager.initializeInvocationCounter(device);
            fail("Should throw exception");
        } catch (final DeviceSessionTerminatedAfterReadingInvocationCounterException e) {
            // expected
        }

        assertThat(device.getInvocationCounter()).isEqualTo(dataObject.getValue());
        verify(this.deviceRepository).save(device);

        assertThat(this.dlmsConnectionStub.isCloseCalled()).isEqualTo(true);
    }

    @Test
    public void resetsInvocationCounter() {
        final DlmsDevice device = new DlmsDeviceBuilder().withInvocationCounter(123).build();

        this.manager.resetInvocationCounter(device);

        assertThat(device.isInvocationCounterInitialized()).isFalse();
        verify(this.deviceRepository).save(device);
    }
}