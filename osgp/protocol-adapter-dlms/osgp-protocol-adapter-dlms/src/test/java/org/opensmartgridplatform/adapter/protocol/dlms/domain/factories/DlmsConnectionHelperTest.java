package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;

@RunWith(MockitoJUnitRunner.class)
public class DlmsConnectionHelperTest {
    private DlmsConnectionHelper helper;

    @Mock
    private InvocationCounterManager invocationCounterManager;

    @Mock
    private DlmsConnectionFactory connectionFactory;

    @Before
    public void setUp() {
        this.helper = new DlmsConnectionHelper(this.invocationCounterManager, this.connectionFactory);
    }

    @Test
    public void createsConnectionForDeviceWithoutInvcationCounter() throws Exception {
        final DlmsDevice device = new DlmsDeviceBuilder().build();
        final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

        final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
        when(this.connectionFactory.getConnection(device, listener)).thenReturn(connectionManager);

        final DlmsConnectionManager result = this.helper.createConnectionForDevice(device, listener);

        assertThat(result).isSameAs(connectionManager);
    }

    @Test
    public void createsConnectionForDeviceWithInvcationCounter() throws Exception {
        final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
        final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

        final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
        when(this.connectionFactory.getConnection(device, listener)).thenReturn(connectionManager);

        final DlmsConnectionManager result = this.helper.createConnectionForDevice(device, listener);

        assertThat(result).isSameAs(connectionManager);

        final InOrder inOrder = inOrder(this.invocationCounterManager, this.connectionFactory);
        inOrder.verify(this.invocationCounterManager).initializeInvocationCounter(device);
        inOrder.verify(this.connectionFactory).getConnection(device, listener);
    }
}