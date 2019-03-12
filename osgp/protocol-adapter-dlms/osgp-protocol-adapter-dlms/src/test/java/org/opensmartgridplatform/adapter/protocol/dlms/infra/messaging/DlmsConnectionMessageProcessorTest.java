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
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@RunWith(MockitoJUnitRunner.class)
public class DlmsConnectionMessageProcessorTest {
    private DlmsConnectionMessageProcessorImpl processor;

    @Mock
    private DlmsConnectionHelper connectionHelper;

    private DlmsMessageListener messageListener;

    @Before
    public void setUp() {
        this.messageListener = new InvocationCountingDlmsMessageListener();
        this.processor = new DlmsConnectionMessageProcessorImpl(this.connectionHelper, this.messageListener);
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
}

