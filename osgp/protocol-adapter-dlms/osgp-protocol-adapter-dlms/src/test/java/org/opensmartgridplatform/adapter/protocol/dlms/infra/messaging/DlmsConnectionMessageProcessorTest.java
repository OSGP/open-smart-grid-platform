/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class DlmsConnectionMessageProcessorTest {
    private DlmsConnectionMessageProcessorImpl processor;

    @Mock
    private DlmsConnectionHelper connectionHelper;

    @Mock
    private DlmsDeviceRepository deviceRepository;

    private DlmsMessageListener messageListener;

    @BeforeEach
    public void setUp() {
        this.messageListener = new InvocationCountingDlmsMessageListener();
        this.processor = new DlmsConnectionMessageProcessorImpl(this.connectionHelper, this.messageListener,
                this.deviceRepository);
    }

    @Test
    public void createsConnectionForDevice() throws Exception {
        final DlmsDevice device = new DlmsDeviceBuilder().build();
        final MessageMetadata metadata = mock(MessageMetadata.class);

        final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
        when(this.connectionHelper.createConnectionForDevice(device, this.messageListener))
                .thenReturn(connectionManager);

        final DlmsConnectionManager result = this.processor.createConnectionForDevice(device, metadata);

        assertThat(result).isSameAs(connectionManager);
    }

    @Test
    public void updatesInvocationCounterWithSuitedListener() {
        final DlmsDevice device = new DlmsDeviceBuilder().withInvocationCounter(111).build();
        final InvocationCountingDlmsMessageListener listener = mock(InvocationCountingDlmsMessageListener.class);
        final DlmsConnectionManager connectionManager = this.connectionManagerWithListener(listener);

        final int numberOfSentMessages = 22;
        when(listener.getNumberOfSentMessages()).thenReturn(numberOfSentMessages);

        this.processor.updateInvocationCounterForDevice(device, connectionManager);

        assertThat(device.getInvocationCounter()).isEqualTo(111 + 22);

        verify(this.deviceRepository).save(device);
    }

    private DlmsConnectionManager connectionManagerWithListener(final DlmsMessageListener listener) {
        return new DlmsConnectionManager(null, null, listener, null);
    }

    @Test
    public void doesNotUpdateInvocationCounterWithUnsuitedListener() {
        final DlmsDevice device = new DlmsDeviceBuilder().withInvocationCounter(111).build();
        final DlmsMessageListener listener = mock(DlmsMessageListener.class);
        final DlmsConnectionManager connectionManager = this.connectionManagerWithListener(listener);

        this.processor.updateInvocationCounterForDevice(device, connectionManager);

        assertThat(device.getInvocationCounter()).isEqualTo(111);
    }
}
