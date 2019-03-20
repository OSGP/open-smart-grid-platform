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
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;

@RunWith(MockitoJUnitRunner.class)
public class InvocationCounterManagerTest {
    private static final AttributeAddress ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE = new AttributeAddress(1,
            new ObisCode(new byte[] { 0, 0, 43, 1, 0, -1 }), 2);

    private InvocationCounterManager manager;

    @Mock
    private DlmsConnectionFactory connectionFactory;

    @Mock
    private DlmsHelperService dlmsHelperService;

    @Mock
    private DlmsDeviceRepository deviceRepository;

    @Before
    public void setUp() {
        this.manager = new InvocationCounterManager(this.connectionFactory, this.dlmsHelperService,
                this.deviceRepository);
    }

    @Test
    public void initializesInvocationCounterForSmrDevice() throws Exception {
        final DlmsDevice device = new DlmsDeviceBuilder().withProtocol("SMR").build();

        final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
        when(this.connectionFactory.getPublicClientConnection(device, null)).thenReturn(connectionManager);

        final DataObject dataObject = DataObject.newInteger32Data(123);
        when(this.dlmsHelperService
                .getAttributeValue(eq(connectionManager), refEq(ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)))
                .thenReturn(dataObject);

        this.manager.initializeInvocationCounter(device);

        assertThat(device.getInvocationCounter()).isEqualTo(dataObject.getValue());
        verify(this.deviceRepository).save(device);
        verify(connectionManager).close();
    }

    @Test
    public void initializesInvocationCounterForNonSmrDevice() throws Exception {
        final DlmsDevice device = new DlmsDeviceBuilder().withProtocol("DSMR").build();

        this.manager.initializeInvocationCounter(device);

        assertThat(device.getInvocationCounter()).isEqualTo(0);
        verify(this.deviceRepository).save(device);
        verifyZeroInteractions(this.connectionFactory);
    }

    @Test
    public void resetsInvocationCounter() {
        final DlmsDevice device = new DlmsDeviceBuilder().withInvocationCounter(123).build();

        this.manager.resetInvocationCounter(device);

        assertThat(device.isInvocationCounterInitialized()).isFalse();
        verify(this.deviceRepository).save(device);
    }
}